package vitisrtlkernel

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import scala.util.Random

class MatMulOptTest extends AnyFreeSpec with ChiselScalatestTester {
  "MatMul should correctly multiply two matrices" in {
    test(new MatMulOpt(64)) { dut =>
      val rand = new Random()
      val n    = 16 // 矩阵大小
      dut.clock.setTimeout(10000)
      // 生成测试数据
      val matrixA = Array.fill(n, n)(rand.nextInt(10)) // 生成0-9的随机数
      val matrixB = Array.fill(n, n)(rand.nextInt(10))

      // 计算期望结果
      val expectedResult = Array.fill(n, n)(0)
      for (i <- 0 until n) {
        for (j <- 0 until n) {
          for (k <- 0 until n) {
            expectedResult(i)(j) += matrixA(i)(k) * matrixB(k)(j)
          }
        }
      }

      // 打印测试数据
      println("Matrix A:")
      for (i <- 0 until n) {
        println(matrixA(i).mkString(" "))
      }
      println("\nMatrix B:")
      for (i <- 0 until n) {
        println(matrixB(i).mkString(" "))
      }

      dut.io.matrixSize.poke(n.U)
      if (dut.io.in1.ready.peek().litToBoolean && dut.io.in2.ready.peek().litToBoolean) {
        dut.io.in1.valid.poke(true.B)
        dut.io.in2.valid.poke(true.B)
        // 设置输入数据
        for (i <- 0 until n) {
          for (j <- 0 until n) {
            dut.io.in1.bits(i)(j).poke(matrixA(i)(j).U)
            dut.io.in2.bits(i)(j).poke(matrixB(i)(j).U)
          }
        }
      } else {
        dut.io.in1.valid.poke(false.B)
        dut.io.in2.valid.poke(false.B)
      }

      // 等待计算完成
      while (!dut.io.out.valid.peek().litToBoolean) {
        dut.clock.step(1)
      }

      dut.io.out.ready.poke(true.B)
      // 验证结果
      println("\nExpected Result:")
      for (i <- 0 until n) {
        println(expectedResult(i).mkString(" "))
      }

      println("\nActual Result:")
      for (i <- 0 until n) {
        for (j <- 0 until n) {
          val actual = dut.io.out.bits(i)(j).peek().litValue.toInt
          print(s"$actual ")
          assert(
            actual == expectedResult(i)(j),
            s"Error at position ($i, $j): expected ${expectedResult(i)(j)}, got $actual"
          )
        }
        println()
      }

      println("\nTest passed!")
    }
  }
}
