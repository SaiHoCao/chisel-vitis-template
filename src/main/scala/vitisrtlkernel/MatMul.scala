package vitisrtlkernel

import chisel3._
import chisel3.util._
import vitisrtlkernel.mmstream._

class MatMul(size: Int = 16) extends Module {
  // kernel的IO接口定义
  val io = IO(new Bundle {
    val dataIF = (new VitisRTLKernelDataIF) // 数据接口，包含读写通道
    val done   = Output(Bool()) // 完成信号
  })

  // 计算实际的矩阵大小
  // readLength * 64 是总字节数，除以4得到32位元素个数，除以2得到单个矩阵的元素个数
  // 然后开平方得到矩阵维度
  val matrixSize = RegInit(0.U(32.W))
  val matrixSizeValid = RegInit(false.B)

  // 读写请求控制寄存器，用于跟踪读写请求的状态
  val readReqIssued_reg  = RegInit(false.B) // 读请求已发出标志，初始为false
  val writeReqIssued_reg = RegInit(false.B) // 写请求已发出标志，初始为false

  // 实例化MM2S和S2MM模块，用于内存到流和流到内存的转换
  val mm2s_module = Module(new MM2S(64, 512)) // 内存到流转换模块，64位地址，512位数据
  val s2mm_module = Module(new S2MM(64, 512)) // 流到内存转换模块，64位地址，512位数据

  // 实例化MaMulOpt模块，使用传入的size参数
  val MatMulOpt = Module(new MatMulOpt(n = size))
  MatMulOpt.io.matrixSize := matrixSize // 连接动态计算的矩阵大小

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
  val queueDepth   = 16 // 队列深度
  val dataQueue    = Module(new Queue(Vec(16, UInt(32.W)), queueDepth))
  val matrixA      = Reg(Vec(size, Vec(size, UInt(32.W)))) // 存储第一个矩阵
  val matrixB      = Reg(Vec(size, Vec(size, UInt(32.W)))) // 存储第二个矩阵
  val rowCount     = RegInit(0.U(32.W)) // 行计数器
  val colCount     = RegInit(0.U(32.W)) // 列计数器
  val last_reg     = RegInit(false.B) // 最后一个数据标志
  val matrixReady  = RegInit(false.B) // 矩阵数据准备就绪标志

  // 状态定义
  val sIdle :: sCalcSize :: sReadA :: sReadB :: sCompute :: sWrite :: Nil = Enum(6)
  val state = RegInit(sIdle)

  // 流控制信号
  mm2s_module.io.streamOut.ready    := false.B
  s2mm_module.io.streamIn.valid     := false.B
  s2mm_module.io.streamIn.bits.data := 0.U
  s2mm_module.io.streamIn.bits.last := false.B

  // 队列连接
  dataQueue.io.enq.valid := false.B
  dataQueue.io.enq.bits  := mm2s_module.io.streamOut.bits.data.asTypeOf(Vec(16, UInt(32.W)))
  dataQueue.io.deq.ready := false.B

  // MatMulOpt连接
  MatMulOpt.io.in1.valid := false.B
  MatMulOpt.io.in2.valid := false.B
  MatMulOpt.io.in1.bits  := matrixA
  MatMulOpt.io.in2.bits  := matrixB
  MatMulOpt.io.out.ready := false.B

  // 状态机逻辑
  switch(state) {
    is(sIdle) {
      when(readReqIssued_reg) {
        state := sCalcSize
      }
    }
    is(sCalcSize) {
      // 计算矩阵大小：readLength * 64 / 4 / 2 得到单个矩阵的元素个数，然后开平方
      val totalElements = (io.dataIF.readLength * 64.U) / 4.U / 2.U
      matrixSize := (totalElements >> 1.U) // 简单的开平方近似，实际应该使用更精确的方法
      matrixSizeValid := true.B
      rowCount := 0.U
      colCount := 0.U
      state := sReadA
    }
    is(sReadA) {
      // 从MM2S读取数据到Queue
      mm2s_module.io.streamOut.ready := dataQueue.io.enq.ready
      dataQueue.io.enq.valid         := mm2s_module.io.streamOut.valid
      dataQueue.io.enq.bits          := mm2s_module.io.streamOut.bits.data.asTypeOf(Vec(16, UInt(32.W)))

      // 从Queue读取数据到matrixA
      when(dataQueue.io.deq.valid) {
        dataQueue.io.deq.ready := true.B
        for (i <- 0 until 16) {
          val idx = rowCount * matrixSize + colCount + i.U
          val row = idx / matrixSize
          val col = idx % matrixSize
          when(row < matrixSize && col < matrixSize) {
            matrixA(row)(col) := dataQueue.io.deq.bits(i)
          }
        }
        colCount := colCount + 16.U

        // 检查是否读取完matrixA的所有数据
        when(colCount + 16.U >= matrixSize) {
          colCount := 0.U
          rowCount := rowCount + 1.U
          when(rowCount + 1.U >= matrixSize) {
            rowCount := 0.U
            colCount := 0.U
            printf(p"matrixA Read Done\n")
            state := sReadB
          }
        }
      }
    }
    is(sReadB) {
      // 从MM2S读取数据到Queue
      mm2s_module.io.streamOut.ready := dataQueue.io.enq.ready
      dataQueue.io.enq.valid         := mm2s_module.io.streamOut.valid
      dataQueue.io.enq.bits          := mm2s_module.io.streamOut.bits.data.asTypeOf(Vec(16, UInt(32.W)))

      // 从Queue读取数据到matrixB
      when(dataQueue.io.deq.valid) {
        dataQueue.io.deq.ready := true.B
        for (i <- 0 until 16) {
          val idx = rowCount * matrixSize + colCount + i.U
          val row = idx / matrixSize
          val col = idx % matrixSize
          when(row < matrixSize && col < matrixSize) {
            matrixB(row)(col) := dataQueue.io.deq.bits(i)
          }
        }
        colCount := colCount + 16.U

        // 检查是否读取完matrixB的所有数据
        when(colCount + 16.U >= matrixSize) {
          colCount := 0.U
          rowCount := rowCount + 1.U
          when(rowCount + 1.U >= matrixSize) {
            rowCount := 0.U
            colCount := 0.U
            printf(p"matrixB Read Done\n")
            state := sCompute
          }
        }
      }
    }
    is(sCompute) {
      // 连接MatMulOpt
      MatMulOpt.io.in1.valid := true.B
      MatMulOpt.io.in2.valid := true.B
      state                  := sWrite
    }
    is(sWrite) {
      when(MatMulOpt.io.out.valid) {
        printf(p"sWrite\n")
        // 准备要写入的数据
        val writeData = Wire(Vec(16, UInt(32.W)))

        // 计算当前要写入的16个元素的位置
        for (i <- 0 until 16) {
          val idx = rowCount * matrixSize + colCount + i.U
          val row = idx / matrixSize
          val col = idx % matrixSize
          printf(p"row: $row, col: $col\n")
          when(row < matrixSize && col < matrixSize) {
            writeData(i) := MatMulOpt.io.out.bits(row)(col)
          }.otherwise {
            writeData(i) := 0.U
          }
        }

        // 写入数据
        s2mm_module.io.streamIn.valid     := true.B
        s2mm_module.io.streamIn.bits.data := writeData.asTypeOf(s2mm_module.io.streamIn.bits.data)
        s2mm_module.io.streamIn.bits.last := (colCount + 16.U >= matrixSize) && (rowCount + 1.U >= matrixSize)

        when(s2mm_module.io.streamIn.ready) {
          colCount := colCount + 16.U

          // 检查是否需要换行
          when(colCount + 16.U >= matrixSize) {
            colCount := 0.U
            rowCount := rowCount + 1.U

            // 检查是否所有数据都已写入
            when(rowCount + 1.U >= matrixSize) {
              rowCount := 0.U
              colCount := 0.U
              state    := sIdle
            }
          }
        }
      }
    }
  }
}

class MatMulOpt(n: Int = 64) extends Module {
  // IO接口定义
  val io = IO(new Bundle {
    val in1 = Flipped(Decoupled(Vec(n, Vec(n, UInt(32.W))))) // 第一个输入矩阵
    val in2 = Flipped(Decoupled(Vec(n, Vec(n, UInt(32.W))))) // 第二个输入矩阵
    val out = Decoupled(Vec(n, Vec(n, UInt(32.W)))) // 输出矩阵
    val matrixSize = Input(UInt(32.W)) // 添加矩阵大小输入
  })

  // ready信号
  val ready_reg = RegInit(true.B) // 存储ready状态
  io.in1.ready := ready_reg
  io.in2.ready := ready_reg

  // 初始化输出
  io.out.valid := false.B
  io.out.bits  := 0.U.asTypeOf(Vec(n, Vec(n, UInt(32.W))))

  // 状态定义
  val sIdle :: sCompute :: sAccumulate :: sUpdate :: sDone :: Nil = Enum(5)
  val state                                                       = RegInit(sIdle)

  // 控制计数器
  val blkSize = 8 // 分块大小
  val numBlks = n / blkSize // 分块数量

  val rowCnt = RegInit(0.U(log2Ceil(n).W)) // 行计数器
  val colCnt = RegInit(0.U(log2Ceil(n).W)) // 列计数器
  val blkCnt = RegInit(0.U(log2Ceil(numBlks max 2).W)) // 分块计数器，确保至少为1位

  // 输入缓存
  val in1_reg = Reg(Vec(n, Vec(n, UInt(32.W)))) // 存储第一个输入矩阵
  val in2_reg = Reg(Vec(n, Vec(n, UInt(32.W)))) // 存储第二个输入矩阵

  // 输出缓存
  val out_reg = Reg(Vec(n, Vec(n, UInt(32.W)))) // 存储输出矩阵

  // 分块数据
  val in1_slice = Wire(Vec(blkSize, UInt(32.W)))
  val in2_slice = Wire(Vec(blkSize, UInt(32.W)))
  // 设置这些向量的默认值
  for (i <- 0 until blkSize) {
    in1_slice(i) := 0.U
    in2_slice(i) := 0.U
  }

  // 累加器
  val accumulator = RegInit(0.U(32.W))

  // 实例化VecMulOpt
  val vecMulOpt = Module(new VecMulOpt(blkSize))

  vecMulOpt.io.in1.valid := false.B
  vecMulOpt.io.in2.valid := false.B
  vecMulOpt.io.in1.bits  := in1_slice
  vecMulOpt.io.in2.bits  := in2_slice
  vecMulOpt.io.out.ready := false.B

  // 状态机逻辑
  switch(state) {
    is(sIdle) {
      rowCnt      := 0.U
      colCnt      := 0.U
      blkCnt      := 0.U
      accumulator := 0.U
      when(io.in1.valid && io.in2.valid) {
        ready_reg := false.B
        in1_reg   := io.in1.bits
        in2_reg   := io.in2.bits
        state     := sCompute
      }
    }

    is(sCompute) {
      printf(p"\n=== Compute State ===")
      printf(p"Current Row: $rowCnt")
      printf(p"Current Col: $colCnt")
      printf(p"Current Block: $blkCnt")
      // 准备分块数据
      for (i <- 0 until blkSize) {
        val idx = blkCnt * blkSize.U + i.U
        when(idx < io.matrixSize) {
          in1_slice(i) := in1_reg(rowCnt)(idx)
          in2_slice(i) := in2_reg(idx)(colCnt)
        }
      }

      // 启动向量乘计算
      vecMulOpt.io.in1.valid := true.B
      vecMulOpt.io.in2.valid := true.B
      state                  := sAccumulate
    }

    is(sAccumulate) {
      when(vecMulOpt.io.out.valid) {
        vecMulOpt.io.out.ready := true.B
        accumulator            := accumulator + vecMulOpt.io.out.bits
        state                  := sUpdate
      }
    }

    is(sUpdate) {
      // 更新分块计数器
      blkCnt := blkCnt + 1.U
      printf(p"\n=== Update State ===")
      printf(p"Current Row: $rowCnt")
      printf(p"Current Col: $colCnt")
      printf(p"Current Block: $blkCnt")
      printf(p"accumulator: $accumulator\n")
      when(blkCnt === (io.matrixSize / blkSize.U - 1.U)) {
        // 当前行列计算完成
        out_reg(rowCnt)(colCnt) := accumulator
        printf(p"out_reg($rowCnt)($colCnt): $accumulator\n")
        accumulator             := 0.U
        blkCnt                  := 0.U
        // 更新行列计数器
        when(colCnt === (io.matrixSize - 1.U)) {
          when(rowCnt === (io.matrixSize - 1.U)) {
            // 所有元素计算完成
            printf(p"\n=== Done State ===")
            printf(p"Current Row: $rowCnt")
            printf(p"Current Col: $colCnt")
            printf(p"Current Block: $blkCnt")
            state := sDone
          }.otherwise {
            // 移动到下一行
            rowCnt := rowCnt + 1.U
            colCnt := 0.U
            state  := sCompute
          }
        }.otherwise {
          // 移动到下一列
          colCnt := colCnt + 1.U
          state  := sCompute
        }
      }.otherwise {
        // 继续计算当前行列的下一个分块
        state := sCompute
      }
    }

    is(sDone) {
      // 输出结果
      io.out.valid := true.B
      io.out.bits  := out_reg
      when(io.out.ready) {
        state := sIdle
      }
    }
  }
}
