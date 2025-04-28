package vitisrtlkernel

import chisel3._
import chisel3.util._
import vitisrtlkernel.mmstream._

class VecAdd extends Module {
  
  // kernel 的 IO是固定的，不要增加其他 port
  val io = IO(new Bundle {
    val dataIF = (new VitisRTLKernelDataIF)
    val done   = Output(Bool())
  })

  // 在 reset 直接开始执行，执行结束后将 done 置位即可
  val readReqIssued_reg  = RegInit(false.B)
  val writeReqIssued_reg = RegInit(false.B)

  val mm2s_module = Module(new MM2S(64, 512))
  val s2mm_module = Module(new S2MM(64, 512))

  mm2s_module.io.axiRead <> io.dataIF.m00Read
  s2mm_module.io.axiWrite <> io.dataIF.m00Write
  mm2s_module.io.req.bits.addr := io.dataIF.readAddress
  mm2s_module.io.req.bits.len  := io.dataIF.readLength
  s2mm_module.io.req.bits.addr := io.dataIF.writeAddress

  mm2s_module.io.req.valid := ~readReqIssued_reg
  s2mm_module.io.req.valid := ~writeReqIssued_reg
  when(mm2s_module.io.req.ready) {
    readReqIssued_reg := true.B
  }
  when(s2mm_module.io.req.ready) {
    writeReqIssued_reg := true.B
  }

  io.done := readReqIssued_reg && writeReqIssued_reg && ~mm2s_module.io.busy && ~s2mm_module.io.busy

  s2mm_module.io.streamIn.valid := mm2s_module.io.streamOut.valid
  mm2s_module.io.streamOut.ready := s2mm_module.io.streamIn.ready
  val inputData_wire = Wire(Vec(16, UInt(32.W)))
  val outputData_wire = Wire(Vec(16, UInt(32.W)))
  inputData_wire := mm2s_module.io.streamOut.bits.data.asTypeOf(inputData_wire)
  outputData_wire.zip(inputData_wire).foreach( p => {
      p._1 := p._2 + 47.U
  })
  s2mm_module.io.streamIn.bits.data := outputData_wire.asUInt
  s2mm_module.io.streamIn.bits.last := mm2s_module.io.streamOut.bits.last
  
}

// vecadd2
// class VecAdd extends Module {

//   // kernel的IO接口定义
//   val io = IO(new Bundle {
//     val dataIF = (new VitisRTLKernelDataIF) // 数据接口，包含读写通道
//     val done   = Output(Bool()) // 完成信号
//   })

//   // 状态机状态定义：空闲->第一次读->第二次读->计算->第一次写->第二次写
//   val sIdle :: sFirstRead :: sSecondRead :: sCompute :: sFirstWrite :: sSecondWrite :: Nil = Enum(6)
//   val state                                                                                = RegInit(sIdle) // 状态寄存器，初始化为空闲状态

//   // 读写请求控制寄存器
//   val readReqIssued_reg  = RegInit(false.B) // 读请求已发出标志
//   val writeReqIssued_reg = RegInit(false.B) // 写请求已发出标志

//   // 实例化MM2S和S2MM模块，用于内存到流和流到内存的转换
//   val mm2s_module = Module(new MM2S(64, 512)) // 内存到流转换模块
//   val s2mm_module = Module(new S2MM(64, 512)) // 流到内存转换模块

//   // AXI接口连接
//   mm2s_module.io.axiRead <> io.dataIF.m00Read // 连接读通道
//   s2mm_module.io.axiWrite <> io.dataIF.m00Write // 连接写通道
//   mm2s_module.io.req.bits.addr := io.dataIF.readAddress // 设置读地址
//   mm2s_module.io.req.bits.len  := io.dataIF.readLength // 设置读长度
//   s2mm_module.io.req.bits.addr := io.dataIF.writeAddress // 设置写地址

//   mm2s_module.io.req.valid := ~readReqIssued_reg
//   s2mm_module.io.req.valid := ~writeReqIssued_reg

//   // 读写请求握手逻辑
//   // 当读请求被接受时，设置读请求已发出标志
//   when(mm2s_module.io.req.ready) {
//     readReqIssued_reg := true.B
//   }
//   // 当写请求被接受时，设置写请求已发出标志
//   when(s2mm_module.io.req.ready) {
//     writeReqIssued_reg := true.B
//   }

//   // 完成信号：读写请求都已发出，且两个模块都不忙时置位
//   io.done := readReqIssued_reg && writeReqIssued_reg && ~mm2s_module.io.busy && ~s2mm_module.io.busy

//   // 数据寄存器定义
//   val firstReadData   = Reg(Vec(16, UInt(32.W))) // 存储第一次读取的16个32位数据
//   val secondReadData  = Reg(Vec(16, UInt(32.W))) // 存储第二次读取的16个32位数据
//   val firstResult     = Reg(Vec(16, UInt(32.W))) // 存储第一次计算的结果
//   val secondResult    = Reg(Vec(16, UInt(32.W))) // 存储第二次计算的结果
//   val firstLast       = RegInit(false.B) // 第一次读取的最后一个数据标志
//   val secondLast      = RegInit(false.B) // 第二次读取的最后一个数据标志
//   val outputData_wire = Wire(Vec(16, UInt(32.W))) // 输出数据线网

//   // 将输入数据转换为16个32位数据的向量
//   val inputData_wire = mm2s_module.io.streamOut.bits.data.asTypeOf(Vec(16, UInt(32.W)))
//   outputData_wire := VecInit(Seq.fill(16)(0.U(32.W))) // 正确初始化16维32位向量为0

//   // // 流控制信号
//   // // 写数据有效信号：在第一次写和第二次写状态时有效
//   // s2mm_module.io.streamIn.valid := mm2s_module.io.streamOut.valid && (state === sFirstWrite || state === sSecondWrite)
//   // // 读数据就绪信号：在第一次读和第二次读状态时有效
//   // mm2s_module.io.streamOut.ready := s2mm_module.io.streamIn.ready && (state === sFirstRead || state === sSecondRead)

//   // // S2mm数据连接
//   // s2mm_module.io.streamIn.bits.data := outputData_wire.asUInt // 连接输出数据
//   // s2mm_module.io.streamIn.bits.last := state === sSecondWrite // 最后一次写时设置last信号

//   // // 完成信号：读写请求都已发出，且两个模块都不忙时置位
//   // io.done := readReqIssued_reg && writeReqIssued_reg && ~mm2s_module.io.busy && ~s2mm_module.io.busy

//   //数据流初始化
//   mm2s_module.io.streamOut.ready    := false.B
//   s2mm_module.io.streamIn.valid     := false.B
//   s2mm_module.io.streamIn.bits.data := 0.U
//   s2mm_module.io.streamIn.bits.last := false.B

//   // 状态机逻辑
//   switch(state) {
//     is(sIdle) {
//       firstLast  := false.B
//       secondLast := false.B
//       // 空闲状态：当读请求已发出时，进入第一次读状态
//       when(readReqIssued_reg) {
//         state := sFirstRead
//       }
//     }
//     is(sFirstRead) {
//       mm2s_module.io.streamOut.ready := true.B
//       // 第一次读状态：当输入数据有效时，存储数据并进入第二次读状态
//       when(mm2s_module.io.streamOut.valid) {
//         firstReadData := inputData_wire
//         when(mm2s_module.io.streamOut.bits.last) { //读到了最后一个数据
//           firstLast := true.B
//         }
//         state := sSecondRead
//       }
//     }
//     is(sSecondRead) {
//       when(firstLast) {
//         secondReadData := 0.U.asTypeOf(secondReadData)
//         state          := sCompute
//       }.otherwise {
//         mm2s_module.io.streamOut.ready := true.B
//         // 第二次读状态：当输入数据有效时，存储数据并进入计算状态
//         when(mm2s_module.io.streamOut.valid) {
//           secondReadData := inputData_wire
//           when(mm2s_module.io.streamOut.bits.last) { //读到了最后一个数据
//             secondLast := true.B
//           }
//           state := sCompute
//         }
//       }
//     }
//     is(sCompute) {
//       // 计算状态：对两组数据进行加47运算
//       for (i <- 0 until 16) {
//         firstResult(i)  := firstReadData(i) + 47.U
//         secondResult(i) := secondReadData(i) + 47.U
//       }
//       when(writeReqIssued_reg) {
//         state := sFirstWrite
//       }
//     }
//     is(sFirstWrite) {
//       s2mm_module.io.streamIn.valid     := true.B
//       s2mm_module.io.streamIn.bits.data := firstResult.asUInt
//       s2mm_module.io.streamIn.bits.last := firstLast 
//       // 第一次写状态：当写通道就绪时，输出第一组结果并进入第二次写状态
//       when(s2mm_module.io.streamIn.ready) {
//         when(firstLast) {//第一个包结束
//           state := sIdle
//         }.otherwise {
//           state := sSecondWrite//写入第二个包
//         }
//       }
//     }
//     is(sSecondWrite) {
//       s2mm_module.io.streamIn.valid     := true.B
//       s2mm_module.io.streamIn.bits.data := secondResult.asUInt
//       s2mm_module.io.streamIn.bits.last := secondLast 
//       // 第二次写状态：当写通道就绪时，输出第二组结果并返回空闲状态
//       when(s2mm_module.io.streamIn.ready) {
//         when(secondLast) {//第二个包结束
//           state := sIdle
//         }.otherwise {
//           state := sFirstRead//没结束，继续读
//         }
//       }
//     }
//   }

// }
