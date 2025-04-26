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
    auto krnl = xrt::kernel(device, xclbin_uuid, "chisel_vecmul");

    wait_for_enter("setup ila and [Enter] to continue...");

    // 定义向量大小
    const size_t VECTOR_SIZE = 32;
    const size_t TOTAL_SIZE = VECTOR_SIZE * 2; // 两个向量的总大小
    
    // 分配内存
    uint32_t input_data[TOTAL_SIZE];  // 存储两个向量
    uint32_t output_data[1];          // 存储点积结果
    
    // 生成测试数据
    for(size_t i = 0; i < VECTOR_SIZE; i++) {
        input_data[i] = i % 10;           // 向量A
        input_data[i + VECTOR_SIZE] = i % 10;  // 向量B
    }
    
    // 计算期望结果（点积）
    uint32_t expected_result = 0;
    for(size_t i = 0; i < VECTOR_SIZE; i++) {
        expected_result += input_data[i] * input_data[i + VECTOR_SIZE];
    }
    
    // 打印输入数据
    std::cout << "Vector A: ";
    for(size_t i = 0; i < VECTOR_SIZE; i++) {
        std::cout << input_data[i] << " ";
    }
    std::cout << std::endl;
    
    std::cout << "Vector B: ";
    for(size_t i = 0; i < VECTOR_SIZE; i++) {
        std::cout << input_data[i + VECTOR_SIZE] << " ";
    }
    std::cout << std::endl;
    
    // allocate buffer on board
    auto read_buffer = xrt::bo(device, TOTAL_SIZE * sizeof(uint32_t), krnl.group_id(1));
    auto write_buffer = xrt::bo(device, sizeof(uint32_t), krnl.group_id(2));
    
    // 输入数据传输到 board
    read_buffer.write(input_data);
    read_buffer.sync(XCL_BO_SYNC_BO_TO_DEVICE);

    // 调用kernel，参数为读取长度（以64字节为单位）
    auto run = krnl(TOTAL_SIZE * sizeof(uint32_t) / 64, read_buffer, write_buffer);
    run.wait();

    // 计算结果从 board read 回 host
    write_buffer.sync(XCL_BO_SYNC_BO_FROM_DEVICE);
    write_buffer.read(output_data);

    // 打印结果
    std::cout << "Expected result: " << expected_result << std::endl;
    std::cout << "Actual result: " << output_data[0] << std::endl;

    // 验证结果
    assert(expected_result == output_data[0]);
    std::cout << "Test passed!" << std::endl;
}