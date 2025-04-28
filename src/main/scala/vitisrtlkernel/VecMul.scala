package vitisrtlkernel

import chisel3._
import chisel3.util._
import vitisrtlkernel.mmstream._

class VecMul(size: Int = 64) extends Module {
  // kernel的IO接口定义
  val io = IO(new Bundle {
    val dataIF = (new VitisRTLKernelDataIF) // 数据接口，包含读写通道
    val done   = Output(Bool()) // 完成信号
  })

  // 读写请求控制寄存器，用于跟踪读写请求的状态
  val readReqIssued_reg  = RegInit(false.B) // 读请求已发出标志，初始为false
  val writeReqIssued_reg = RegInit(false.B) // 写请求已发出标志，初始为false

  // 实例化MM2S和S2MM模块，用于内存到流和流到内存的转换
  val mm2s_module = Module(new MM2S(64, 512)) // 内存到流转换模块，64位地址，512位数据
  val s2mm_module = Module(new S2MM(64, 512)) // 流到内存转换模块，64位地址，512位数据

  // 实例化VecMulOpt模块，使用传入的size参数
  val vecMulOpt = Module(new VecMulOpt(size))

  // AXI接口连接
  mm2s_module.io.axiRead <> io.dataIF.m00Read // 连接读通道
  s2mm_module.io.axiWrite <> io.dataIF.m00Write // 连接写通道
  mm2s_module.io.req.bits.addr := io.dataIF.readAddress // 设置读地址
  mm2s_module.io.req.bits.len  := io.dataIF.readLength // 设置读长度
  s2mm_module.io.req.bits.addr := io.dataIF.writeAddress // 设置写地址

  // 读写请求控制逻辑
  // 当读请求未发出时，读请求有效
  mm2s_module.io.req.valid := ~readReqIssued_reg
  // 当写请求未发出时，写请求有效
  s2mm_module.io.req.valid := ~writeReqIssued_reg

  // 读写请求握手逻辑
  // 当读请求被接受时，设置读请求已发出标志
  when(mm2s_module.io.req.ready) {
    readReqIssued_reg := true.B
  }
  // 当写请求被接受时，设置写请求已发出标志
  when(s2mm_module.io.req.ready) {
    writeReqIssued_reg := true.B
  }

  // 完成信号：当读写请求都已发出且两个模块都不忙时置位
  io.done := readReqIssued_reg && writeReqIssued_reg && ~mm2s_module.io.busy && ~s2mm_module.io.busy

  // 数据队列和向量寄存器
  val queueDepth  = 16 // 队列深度
  val dataQueue   = Module(new Queue(Vec(16, UInt(32.W)), queueDepth))
  val vectorA     = Reg(Vec(size, UInt(32.W))) // 存储第一个向量
  val vectorB     = Reg(Vec(size, UInt(32.W))) // 存储第二个向量
  val count       = RegInit(0.U(32.W)) // 数据计数器
  val last_reg    = RegInit(false.B) // 最后一个数据标志
  val vectorReady = RegInit(false.B) // 向量数据准备就绪标志

  // 状态定义
  val sIdle :: sReadA :: sReadB :: sCompute :: sWrite :: Nil = Enum(5)
  val state                                                  = RegInit(sIdle)

  // 流控制信号
  mm2s_module.io.streamOut.ready    := false.B
  s2mm_module.io.streamIn.valid     := false.B
  s2mm_module.io.streamIn.bits.data := 0.U
  s2mm_module.io.streamIn.bits.last := false.B

  // 队列连接
  dataQueue.io.enq.valid := false.B
  dataQueue.io.enq.bits  := mm2s_module.io.streamOut.bits.data.asTypeOf(Vec(16, UInt(32.W)))
  dataQueue.io.deq.ready := false.B

  // VecMulOpt连接
  vecMulOpt.io.in1.valid := false.B
  vecMulOpt.io.in1.bits  := 0.U.asTypeOf(Vec(size, UInt(32.W)))
  vecMulOpt.io.in2.valid := false.B
  vecMulOpt.io.in2.bits  := 0.U.asTypeOf(Vec(size, UInt(32.W)))
  vecMulOpt.io.out.ready := false.B

  // 状态机逻辑
  switch(state) {
    is(sIdle) {
      when(readReqIssued_reg) {
        count := 0.U
        state := sReadA
      }
    }
    is(sReadA) {
      // 从MM2S读取数据到Queue
      mm2s_module.io.streamOut.ready := dataQueue.io.enq.ready
      dataQueue.io.enq.valid         := mm2s_module.io.streamOut.valid
      dataQueue.io.enq.bits          := mm2s_module.io.streamOut.bits.data.asTypeOf(Vec(16, UInt(32.W)))

      // 从Queue读取数据到vectorA
      when(dataQueue.io.deq.valid) {
        dataQueue.io.deq.ready := true.B
        for (i <- 0 until 16) {
          vectorA(count + i.U) := dataQueue.io.deq.bits(i)
        }
        count := count + 16.U

        // 检查是否读取完vectorA的所有数据
        when(count + 16.U >= size.U) {
          count := 0.U
          state := sReadB
        }

      }
    }
    is(sReadB) {
      // 从MM2S读取数据到Queue
      mm2s_module.io.streamOut.ready := dataQueue.io.enq.ready
      dataQueue.io.enq.valid         := mm2s_module.io.streamOut.valid
      dataQueue.io.enq.bits          := mm2s_module.io.streamOut.bits.data.asTypeOf(Vec(16, UInt(32.W)))

      // 从Queue读取数据到vectorB
      when(dataQueue.io.deq.valid) {
        dataQueue.io.deq.ready := true.B
        for (i <- 0 until 16) {
          vectorB(count + i.U) := dataQueue.io.deq.bits(i)
        }
        count := count + 16.U
        // printf(p"mm2s_module.io.streamOut.bits.last: ${mm2s_module.io.streamOut.bits.last}\n")

        // 检查是否读取完vectorB的所有数据
        when(count + 16.U >= size.U) {
          count := 0.U
          // when(mm2s_module.io.streamOut.bits.last) {
          state := sCompute
          // }
        }
      }
    }
    is(sCompute) {
      // 连接VecMulOpt
      vecMulOpt.io.in1.valid := true.B
      vecMulOpt.io.in2.valid := true.B
      vecMulOpt.io.in1.bits  := vectorA
      vecMulOpt.io.in2.bits  := vectorB
      vecMulOpt.io.out.ready := true.B

      when(vecMulOpt.io.out.valid) {
        s2mm_module.io.streamIn.valid     := true.B
        s2mm_module.io.streamIn.bits.data := Cat(0.U(480.W), vecMulOpt.io.out.bits)
        s2mm_module.io.streamIn.bits.last := true.B

        when(s2mm_module.io.streamIn.ready) {
          state := sIdle
        }
      }
    }
  }
}

class VecMulOpt(size: Int = 32) extends Module {
  // IO接口定义
  val io = IO(new Bundle {
    val in1 = Flipped(Decoupled(Vec(size, UInt(32.W)))) // 第一个输入向量
    val in2 = Flipped(Decoupled(Vec(size, UInt(32.W)))) // 第二个输入向量
    val out = Decoupled(UInt(32.W)) // 输出标量结果
  })

  // 内部寄存器
  val in1_reg   = Reg(Vec(size, UInt(32.W))) // 存储第一个输入向量
  val in2_reg   = Reg(Vec(size, UInt(32.W))) // 存储第二个输入向量
  val valid_reg = RegInit(false.B) // 存储valid状态

  val ready_reg = RegInit(true.B) // 存储ready状态
  // 输出ready信号
  io.in1.ready := ready_reg
  io.in2.ready := ready_reg

  // 当输入都有效时，接收数据
  when(io.in1.valid && io.in2.valid) {
    ready_reg := false.B
    in1_reg   := io.in1.bits
    in2_reg   := io.in2.bits
    valid_reg := true.B
  }

  // 计算点积结果
  val products = Wire(Vec(size, UInt(32.W))) // 存储每个元素的乘积
  for (i <- 0 until size) {
    products(i) := in1_reg(i) * in2_reg(i)
  }

  // 计算所有乘积的和
  val sum = products.reduce(_ + _)

  // 输出连接
  io.out.valid := valid_reg
  io.out.bits  := sum

  // 当输出被接收时，重置valid状态
  when(io.out.ready && valid_reg) {
    valid_reg := false.B
    ready_reg := true.B
  }
}
