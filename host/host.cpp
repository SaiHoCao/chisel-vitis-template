#include <xrt/xrt_kernel.h>
#include <xrt/xrt_bo.h>
#include <iostream>
#include <assert.h>

void wait_for_enter(const std::string &msg) {
    std::cout << msg << std::endl;
    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
}

// int main(int argc, char **args)
// {
//     std::cout << args[1] << std::endl;

//     /**
//      * @brief how to determine device_index
//      * TIP: The device ID can be obtained using the xbutil  command for a specific accelerator.
//      */
//     unsigned int device_index = 0; 
//     std::cout << "Open the device" << device_index << std::endl;
//     auto device = xrt::device(device_index);

//     std::string xclbin_path(args[1]);
//     std::cout << "Load the xclbin " << xclbin_path << std::endl;
//     auto xclbin_uuid = device.load_xclbin(xclbin_path);

//     // instantiate kernel
//     // auto krnl = xrt::kernel(device, xclbin_uuid, "rtl_kernel_wizard_0");
//     auto krnl = xrt::kernel(device, xclbin_uuid, "chisel_vecadd2");

//     wait_for_enter("setup ila and [Enter] to continue...");

    
//     size_t data_num = 4096;
//     uint32_t input_data[data_num];
//     uint32_t output_data[data_num];
//     for(size_t i = 0; i < data_num; i++){
//         input_data[i] = i % 128;
//     }
    
//     // allocate buffer on board
//     auto read_buffer = xrt::bo(device, data_num * sizeof(uint32_t), krnl.group_id(1));
//     auto write_buffer = xrt::bo(device, data_num * sizeof(uint32_t), krnl.group_id(2));
    
//     // 输入数据传输到 board
//     read_buffer.write(input_data);
//     read_buffer.sync(XCL_BO_SYNC_BO_TO_DEVICE);

//     auto run = krnl(data_num * 4 / 64, read_buffer, write_buffer);
//     run.wait();

//     // 计算结果从 board read 回 host
//     write_buffer.sync(XCL_BO_SYNC_BO_FROM_DEVICE);
//     write_buffer.read(output_data);

//     // check result
//     for(size_t i = 0; i < data_num; i++){
//         assert(input_data[i] + 47 == output_data[i]);
//         std::cout << "input:" << input_data[i] << " output:" << output_data[i] << std::endl;
//     } 
// }

int main(int argc, char **args)
{
    std::cout << args[1] << std::endl;

    /**
     * @brief how to determine device_index
     * TIP: The device ID can be obtained using the xbutil  command for a specific accelerator.
     */
    unsigned int device_index = 0; 
    std::cout << "Open the device" << device_index << std::endl;
    auto device = xrt::device(device_index);

    std::string xclbin_path(args[1]);
    std::cout << "Load the xclbin " << xclbin_path << std::endl;
    auto xclbin_uuid = device.load_xclbin(xclbin_path);

    // instantiate kernel
    auto krnl = xrt::kernel(device, xclbin_uuid, "vecmul_size");

    wait_for_enter("setup ila and [Enter] to continue...");

    // 定义矩阵大小
    const size_t MATRIX_SIZE = 16;  // 16x16的矩阵
    const size_t TOTAL_SIZE = MATRIX_SIZE * MATRIX_SIZE * 2; // 两个矩阵的总大小
    
    // 分配内存
    uint32_t input_data[TOTAL_SIZE];  // 存储两个矩阵
    uint32_t output_data[MATRIX_SIZE * MATRIX_SIZE];  // 存储结果矩阵
    
    // 生成测试数据
    for(size_t i = 0; i < MATRIX_SIZE; i++) {
        for(size_t j = 0; j < MATRIX_SIZE; j++) {
            // 矩阵A
            input_data[i * MATRIX_SIZE + j] = (i + j) % 10;
            // 矩阵B
            input_data[MATRIX_SIZE * MATRIX_SIZE + i * MATRIX_SIZE + j] = (i * j) % 10;
        }
    }
    
    // 计算期望结果（矩阵乘法）
    uint32_t expected_result[MATRIX_SIZE][MATRIX_SIZE] = {0};
    for(size_t i = 0; i < MATRIX_SIZE; i++) {
        for(size_t j = 0; j < MATRIX_SIZE; j++) {
            for(size_t k = 0; k < MATRIX_SIZE; k++) {
                expected_result[i][j] += input_data[i * MATRIX_SIZE + k] * 
                                       input_data[MATRIX_SIZE * MATRIX_SIZE + k * MATRIX_SIZE + j];
            }
        }
    }
    
    // 打印输入数据
    std::cout << "Matrix A:" << std::endl;
    for(size_t i = 0; i < MATRIX_SIZE; i++) {
        for(size_t j = 0; j < MATRIX_SIZE; j++) {
            std::cout << input_data[i * MATRIX_SIZE + j] << " ";
        }
        std::cout << std::endl;
    }
    
    std::cout << "\nMatrix B:" << std::endl;
    for(size_t i = 0; i < MATRIX_SIZE; i++) {
        for(size_t j = 0; j < MATRIX_SIZE; j++) {
            std::cout << input_data[MATRIX_SIZE * MATRIX_SIZE + i * MATRIX_SIZE + j] << " ";
        }
        std::cout << std::endl;
    }
    
    // allocate buffer on board
    auto read_buffer = xrt::bo(device, TOTAL_SIZE * sizeof(uint32_t), krnl.group_id(1));
    auto write_buffer = xrt::bo(device, MATRIX_SIZE * MATRIX_SIZE * sizeof(uint32_t), krnl.group_id(2));
    
    // 输入数据传输到 board
    read_buffer.write(input_data);
    read_buffer.sync(XCL_BO_SYNC_BO_TO_DEVICE);

    // 调用kernel，参数为读取长度（以64字节为单位）
    auto run = krnl(TOTAL_SIZE * sizeof(uint32_t) / 64, read_buffer, write_buffer,MATRIX_SIZE);
    run.wait();

    // 计算结果从 board read 回 host
    write_buffer.sync(XCL_BO_SYNC_BO_FROM_DEVICE);
    write_buffer.read(output_data);

    // 打印结果
    std::cout << "\nExpected result:" << std::endl;
    for(size_t i = 0; i < MATRIX_SIZE; i++) {
        for(size_t j = 0; j < MATRIX_SIZE; j++) {
            std::cout << expected_result[i][j] << " ";
        }
        std::cout << std::endl;
    }
    
    std::cout << "\nActual result:" << std::endl;
    for(size_t i = 0; i < MATRIX_SIZE; i++) {
        for(size_t j = 0; j < MATRIX_SIZE; j++) {
            std::cout << output_data[i * MATRIX_SIZE + j] << " ";
        }
        std::cout << std::endl;
    }

    // 验证结果
    bool all_correct = true;
    for(size_t i = 0; i < MATRIX_SIZE; i++) {
        for(size_t j = 0; j < MATRIX_SIZE; j++) {
            if(expected_result[i][j] != output_data[i * MATRIX_SIZE + j]) {
                std::cout << "Error at position (" << i << ", " << j << "): "
                          << "Expected " << expected_result[i][j] 
                          << ", Actual " << output_data[i * MATRIX_SIZE + j] << std::endl;
                all_correct = false;
            }
        }
    }
    
    assert(all_correct);
    std::cout << "Test passed!" << std::endl;
}