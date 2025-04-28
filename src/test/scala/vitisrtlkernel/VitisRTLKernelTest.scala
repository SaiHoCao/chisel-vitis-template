package vitisrtlkernel

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import scala.util.Random

import vitisrtlkernel.mmstream.MemSim
import vitisrtlkernel.mmstream.AXIReadSlaveSim
import vitisrtlkernel.mmstream.AXIWriteSlaveSim

class VitisRTLKernelTest extends AnyFreeSpec with ChiselScalatestTester {

  class VitisRTLKernelWrapper extends Module {
    val io = IO(new Bundle {
      val ap_start = Input(Bool())
      val ap_idle  = Output(Bool())
      val ap_done  = Output(Bool())
      val ap_ready = Output(Bool())
      val dataIF   = new VitisRTLKernelDataIF
      val done     = Output(Bool())
    })

    val vitisRTLKernel = Module(new VitisRTLKernel)
    vitisRTLKernel.ap_clk   := clock
    vitisRTLKernel.ap_start := io.ap_start
    io.ap_idle              := vitisRTLKernel.ap_idle
    io.ap_done              := vitisRTLKernel.ap_done
    io.ap_ready             := vitisRTLKernel.ap_ready
    vitisRTLKernel.dataIF <> io.dataIF

    val done_reg = RegInit(false.B)
    when(vitisRTLKernel.ap_done) {
      done_reg := true.B
    }
    io.done := done_reg
  }

  "KernelExecution" in {
    test(new VitisRTLKernelWrapper)
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        val rand    = new Random()
        val readMem = new MemSim(16 * 1024 * 1024)
        readMem.randomInit()
        val writeMem      = new MemSim(16 * 1024 * 1024)
        val readAddress   = 64 * rand.nextInt(1024)
        val writeAddress  = 64 * rand.nextInt(1024)
        val readLength    = rand.nextInt(1024)
        val axiReadSlave  = new AXIReadSlaveSim(readMem, dut.io.dataIF.m00Read, dut.clock, dut.io.done, true, true)
        val axiWriteSlave = new AXIWriteSlaveSim(writeMem, dut.io.dataIF.m00Write, dut.clock, dut.io.done, true, true)
        fork {
          dut.io.dataIF.readAddress.poke(readAddress.U)
          dut.io.dataIF.readLength.poke(readLength.U)
          dut.io.dataIF.writeAddress.poke(writeAddress.U)
          dut.io.ap_start.poke(false.B)
          dut.clock.step(2)
          dut.io.ap_start.poke(true.B)
          while(!dut.io.done.peek().litToBoolean){
            dut.clock.step(1)
          }
          dut.io.ap_start.poke(false.B)
        }.fork {
          axiReadSlave.serve()
        }.fork {
          axiWriteSlave.serve()
        }.join()
        // 检查正确性
        for (i <- 0 until readLength / 4 * 64) {
          assert((readMem.read(readAddress + i * 4, 4) + 47) % BigInt("ffffffff", 16) == writeMem.read(writeAddress + i * 4, 4))
        }
      }
  }
}

class VitisRTLKernelTest_VecMul extends AnyFreeSpec with ChiselScalatestTester {

  class VitisRTLKernelWrapper extends Module {
    val io = IO(new Bundle {
      val ap_start = Input(Bool())
      val ap_idle  = Output(Bool())
      val ap_done  = Output(Bool())
      val ap_ready = Output(Bool())
      val dataIF   = new VitisRTLKernelDataIF
      val done     = Output(Bool())
    })

    val vitisRTLKernel = Module(new VitisRTLKernel)
    vitisRTLKernel.ap_clk   := clock
    vitisRTLKernel.ap_start := io.ap_start
    io.ap_idle              := vitisRTLKernel.ap_idle
    io.ap_done              := vitisRTLKernel.ap_done
    io.ap_ready             := vitisRTLKernel.ap_ready
    vitisRTLKernel.dataIF <> io.dataIF

    val done_reg = RegInit(false.B)
    when(vitisRTLKernel.ap_done) {
      done_reg := true.B
    }
    io.done := done_reg
  }

  "KernelExecution" in {
    test(new VitisRTLKernelWrapper)
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        val rand       = new Random()
        val vectorSize = 64 // 向量大小
        val readMem    = new MemSim(16 * 1024 * 1024)
        val writeMem   = new MemSim(16 * 1024 * 1024)

        // 生成测试数据
        val vectorA = Array.fill(vectorSize)(rand.nextInt(10))
        val vectorB = Array.fill(vectorSize)(rand.nextInt(10))

        // 计算期望结果（点积）
        val expectedResult = vectorA.zip(vectorB).map { case (a, b) => a * b }.sum

        // 将数据写入内存
        val readAddress  = 64 * rand.nextInt(1024)
        val writeAddress = 64 * rand.nextInt(1024)

        // 写入向量A
        for (i <- 0 until vectorSize) {
          readMem.write(readAddress + i * 4, vectorA(i))
        }

        // 写入向量B
        for (i <- 0 until vectorSize) {
          readMem.write(readAddress + vectorSize * 4 + i * 4, vectorB(i))
        }

        val axiReadSlave  = new AXIReadSlaveSim(readMem, dut.io.dataIF.m00Read, dut.clock, dut.io.done, true, true)
        val axiWriteSlave = new AXIWriteSlaveSim(writeMem, dut.io.dataIF.m00Write, dut.clock, dut.io.done, true, true)

        fork {
          // 设置读取长度（两个向量的总大小）
          dut.io.dataIF.readAddress.poke(readAddress.U)
          dut.io.dataIF.readLength.poke((vectorSize * 4 * 2 / 64).U) // 每个向量vectorSize个元素，每个元素4字节 两个向量但是 一次length读64个字节
          dut.io.dataIF.writeAddress.poke(writeAddress.U)
          dut.io.ap_start.poke(false.B)
          dut.clock.step(2)
          dut.io.ap_start.poke(true.B)
          while (!dut.io.done.peek().litToBoolean) {
            dut.clock.step(1)
          }
          dut.io.ap_start.poke(false.B)
        }.fork {
          axiReadSlave.serve()
        }.fork {
          axiWriteSlave.serve()
        }.join()

        // 检查正确性
        val actualResult = writeMem.read(writeAddress, 4)
        assert(actualResult == expectedResult, s"Expected result: $expectedResult, Actual result: $actualResult")
        println(s"Expected result: $expectedResult, Actual result: $actualResult")
      }
  }
}

class VitisRTLKernelTest_MatMul extends AnyFreeSpec with ChiselScalatestTester {

  class VitisRTLKernelWrapper extends Module {
    val io = IO(new Bundle {
      val ap_start = Input(Bool())
      val ap_idle  = Output(Bool())
      val ap_done  = Output(Bool())
      val ap_ready = Output(Bool())
      val dataIF   = new VitisRTLKernelDataIF
      val done     = Output(Bool())
    })

    val vitisRTLKernel = Module(new VitisRTLKernel)
    vitisRTLKernel.ap_clk   := clock
    vitisRTLKernel.ap_start := io.ap_start
    io.ap_idle              := vitisRTLKernel.ap_idle
    io.ap_done              := vitisRTLKernel.ap_done
    io.ap_ready             := vitisRTLKernel.ap_ready
    vitisRTLKernel.dataIF <> io.dataIF

    val done_reg = RegInit(false.B)
    when(vitisRTLKernel.ap_done) {
      done_reg := true.B
    }
    io.done := done_reg
  }

  "KernelExecution" in {
    test(new VitisRTLKernelWrapper)
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.clock.setTimeout(2000)
        val rand       = new Random()
        val matrixSize = 16 // 矩阵大小
        val readMem    = new MemSim(16 * 1024 * 1024)
        val writeMem   = new MemSim(16 * 1024 * 1024)

        // 生成测试数据
        val matrixA = Array.fill(matrixSize, matrixSize)(rand.nextInt(10))
        val matrixB = Array.fill(matrixSize, matrixSize)(rand.nextInt(10))

        // 计算期望结果（矩阵乘法）
        val expectedResult = Array.ofDim[Int](matrixSize, matrixSize)
        for (i <- 0 until matrixSize) {
          for (j <- 0 until matrixSize) {
            var sum = 0
            for (k <- 0 until matrixSize) {
              sum += matrixA(i)(k) * matrixB(k)(j)
            }
            expectedResult(i)(j) = sum
          }
        }

        // 将数据写入内存
        val readAddress  = 64 * rand.nextInt(1024)
        val writeAddress = 64 * rand.nextInt(1024)

        // 写入矩阵A（按行优先顺序）
        for (i <- 0 until matrixSize) {
          for (j <- 0 until matrixSize) {
            readMem.write(readAddress + (i * matrixSize + j) * 4, matrixA(i)(j))
          }
        }

        // 写入矩阵B（按行优先顺序）
        val matrixBOffset = matrixSize * matrixSize * 4
        for (i <- 0 until matrixSize) {
          for (j <- 0 until matrixSize) {
            readMem.write(readAddress + matrixBOffset + (i * matrixSize + j) * 4, matrixB(i)(j))
          }
        }

        val axiReadSlave  = new AXIReadSlaveSim(readMem, dut.io.dataIF.m00Read, dut.clock, dut.io.done, true, true)
        val axiWriteSlave = new AXIWriteSlaveSim(writeMem, dut.io.dataIF.m00Write, dut.clock, dut.io.done, true, true)

        fork {
          // 设置读取长度（两个矩阵的总大小）
          dut.io.dataIF.readAddress.poke(readAddress.U)
          dut.io.dataIF.readLength.poke((matrixSize * matrixSize * 4 * 2 / 64).U) // 每个矩阵matrixSize*matrixSize个元素，每个元素4字节，两个矩阵
          dut.io.dataIF.writeAddress.poke(writeAddress.U)
          dut.io.ap_start.poke(false.B)
          dut.clock.step(2)
          dut.io.ap_start.poke(true.B)
          while (!dut.io.done.peek().litToBoolean) {
            dut.clock.step(1)
          }
          dut.io.ap_start.poke(false.B)
        }.fork {
          axiReadSlave.serve()
        }.fork {
          axiWriteSlave.serve()
        }.join()

        // 检查正确性
        var allCorrect = true
        for (i <- 0 until matrixSize) {
          for (j <- 0 until matrixSize) {
            val actualResult = writeMem.read(writeAddress + (i * matrixSize + j) * 4, 4)
            if (actualResult != expectedResult(i)(j)) {
              println(s"Error at position ($i, $j): Expected ${expectedResult(i)(j)}, Actual $actualResult")
              allCorrect = false
            }
          }
        }
        assert(allCorrect, "Matrix multiplication result verification failed")
        println("Matrix multiplication test passed!")
      }
  }
}

