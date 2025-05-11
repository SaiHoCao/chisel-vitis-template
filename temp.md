source /tools/Xilinx/Vitis/2023.2/settings64.sh

source /opt/xilinx/xrt/setup.sh

which vitis

vitis #启动IDE

  VitisRTLKernel example(
    .ap_clk          ( ap_clk          ),
    .ap_start        ( ap_start        ),
    .ap_done         ( ap_done         ),
    .ap_idle         ( ap_idle         ),
    .ap_ready        ( ap_ready        ),
    .dataIF_readLength            ( readLength      ),
    .dataIF_readAddress           ( readAddress     ),
    .dataIF_writeAddress          ( writeAddress    ),
    .dataIF_matrixSize            ( matrixSize      ),
    .dataIF_m00Read_ar_ready      (m00_axi_arready),
    .dataIF_m00Read_ar_valid      (m00_axi_arvalid),
    .dataIF_m00Read_ar_bits_addr  (m00_axi_araddr),
    .dataIF_m00Read_ar_bits_len   (m00_axi_arlen),
    .dataIF_m00Read_r_ready       (m00_axi_rready),
    .dataIF_m00Read_r_valid       (m00_axi_rvalid),
    .dataIF_m00Read_r_bits_data   (m00_axi_rdata),
    .dataIF_m00Read_r_bits_last   (m00_axi_rlast),
    .dataIF_m00Write_aw_ready     (m00_axi_awready),
    .dataIF_m00Write_aw_valid     (m00_axi_awvalid),
    .dataIF_m00Write_aw_bits_addr (m00_axi_awaddr),
    .dataIF_m00Write_aw_bits_len  (m00_axi_awlen),
    .dataIF_m00Write_w_ready      (m00_axi_wready),
    .dataIF_m00Write_w_valid      (m00_axi_wvalid),
    .dataIF_m00Write_w_bits_data  (m00_axi_wdata),
    .dataIF_m00Write_w_bits_strb  (m00_axi_wstrb),
    .dataIF_m00Write_w_bits_last  (m00_axi_wlast),
    .dataIF_m00Write_b_ready      (m00_axi_bready),
    .dataIF_m00Write_b_valid      (m00_axi_bvalid)
  );


make 



*** Running vivado
    with args -log vecmul_size.vds -m64 -product Vivado -mode batch -messageDb vivado.pb -notrace -source vecmul_size.tcl


****** Vivado v2023.2 (64-bit)
  **** SW Build 4029153 on Fri Oct 13 20:13:54 MDT 2023
  **** IP Build 4028589 on Sat Oct 14 00:45:43 MDT 2023
  **** SharedData Build 4025554 on Tue Oct 10 17:18:54 MDT 2023
    ** Copyright 1986-2022 Xilinx, Inc. All Rights Reserved.
    ** Copyright 2022-2023 Advanced Micro Devices, Inc. All Rights Reserved.

source vecmul_size.tcl -notrace
create_project: Time (s): cpu = 00:00:23 ; elapsed = 00:00:23 . Memory (MB): peak = 1335.113 ; gain = 21.836 ; free physical = 36053 ; free virtual = 339307
Command: synth_design -top vecmul_size -part xcu280-fsvh2892-2L-e -mode out_of_context
Starting synth_design
Attempting to get a license for feature 'Synthesis' and/or device 'xcu280'
INFO: [Common 17-349] Got license for feature 'Synthesis' and/or device 'xcu280'
INFO: [Device 21-403] Loading part xcu280-fsvh2892-2L-e
INFO: [Synth 8-7079] Multithreading enabled for synth_design using a maximum of 4 processes.
INFO: [Synth 8-7078] Launching helper process for spawning children vivado processes
INFO: [Synth 8-7075] Helper process launched with PID 275600
---------------------------------------------------------------------------------
Starting RTL Elaboration : Time (s): cpu = 00:00:11 ; elapsed = 00:00:12 . Memory (MB): peak = 3092.484 ; gain = 398.715 ; free physical = 33427 ; free virtual = 336909
---------------------------------------------------------------------------------
INFO: [Synth 8-6157] synthesizing module 'vecmul_size' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size.v:7]
INFO: [Synth 8-6157] synthesizing module 'vecmul_size_control_s_axi' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size_control_s_axi.v:9]
	Parameter C_S_AXI_ADDR_WIDTH bound to: 12 - type: integer 
	Parameter C_S_AXI_DATA_WIDTH bound to: 32 - type: integer 
INFO: [Synth 8-155] case statement is not full and has no default [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size_control_s_axi.v:237]
INFO: [Synth 8-6155] done synthesizing module 'vecmul_size_control_s_axi' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size_control_s_axi.v:9]
INFO: [Synth 8-6157] synthesizing module 'VitisRTLKernel' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:180130]
INFO: [Synth 8-6157] synthesizing module 'MatMul' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:60596]
INFO: [Synth 8-6157] synthesizing module 'MM2S' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:164]
INFO: [Synth 8-6157] synthesizing module 'Queue' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:1]
INFO: [Synth 8-6155] done synthesizing module 'Queue' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:1]
INFO: [Synth 8-6155] done synthesizing module 'MM2S' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:164]
INFO: [Synth 8-6157] synthesizing module 'S2MM' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:436]
INFO: [Synth 8-6155] done synthesizing module 'S2MM' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:436]
INFO: [Synth 8-6157] synthesizing module 'MatMulOpt' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:938]
INFO: [Synth 8-6157] synthesizing module 'VecMulOpt' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:712]
INFO: [Synth 8-6155] done synthesizing module 'VecMulOpt' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:712]
INFO: [Synth 8-6155] done synthesizing module 'MatMulOpt' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:938]
INFO: [Synth 8-6157] synthesizing module 'Queue_2' [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:60085]
INFO: [Synth 8-6155] done synthesizing module 'Queue_2' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:60085]
INFO: [Synth 8-6155] done synthesizing module 'MatMul' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:60596]
INFO: [Synth 8-6155] done synthesizing module 'VitisRTLKernel' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/vecmul_size_ex.srcs/sources_1/imports/chisel_matmul_size/VitisRTLKernel.v:180130]
INFO: [Synth 8-6155] done synthesizing module 'vecmul_size' (0#1) [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size.v:7]
WARNING: [Synth 8-6014] Unused sequential element int_ap_done_reg was removed.  [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size_control_s_axi.v:322]
WARNING: [Synth 8-7129] Port io_req_bits_addr[5] in module S2MM is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[4] in module S2MM is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[3] in module S2MM is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[2] in module S2MM is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[1] in module S2MM is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[0] in module S2MM is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[5] in module MM2S is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[4] in module MM2S is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[3] in module MM2S is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[2] in module MM2S is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[1] in module MM2S is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_req_bits_addr[0] in module MM2S is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[63] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[62] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[61] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[60] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[59] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[58] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[57] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[56] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[55] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[54] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[53] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[52] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[51] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[50] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[49] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[48] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[47] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[46] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[45] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[44] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[43] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[42] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[41] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[40] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[39] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[38] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[37] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[36] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[35] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[34] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[33] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port io_dataIF_readLength[32] in module MatMul is either unconnected or has no load
WARNING: [Synth 8-7129] Port AWADDR[11] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port AWADDR[10] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port AWADDR[9] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port AWADDR[8] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port AWADDR[7] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port AWADDR[6] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port ARADDR[11] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port ARADDR[10] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port ARADDR[9] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port ARADDR[8] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port ARADDR[7] in module vecmul_size_control_s_axi is either unconnected or has no load
WARNING: [Synth 8-7129] Port ARADDR[6] in module vecmul_size_control_s_axi is either unconnected or has no load
---------------------------------------------------------------------------------
Finished RTL Elaboration : Time (s): cpu = 00:09:14 ; elapsed = 00:09:47 . Memory (MB): peak = 8497.219 ; gain = 5803.449 ; free physical = 20077 ; free virtual = 324703
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
Start Handling Custom Attributes
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
Finished Handling Custom Attributes : Time (s): cpu = 00:09:48 ; elapsed = 00:10:25 . Memory (MB): peak = 8497.219 ; gain = 5803.449 ; free physical = 20044 ; free virtual = 324679
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
Finished RTL Optimization Phase 1 : Time (s): cpu = 00:09:48 ; elapsed = 00:10:25 . Memory (MB): peak = 8497.219 ; gain = 5803.449 ; free physical = 20044 ; free virtual = 324679
---------------------------------------------------------------------------------
Netlist sorting complete. Time (s): cpu = 00:00:33 ; elapsed = 00:00:33 . Memory (MB): peak = 8568.227 ; gain = 20.758 ; free physical = 19214 ; free virtual = 324033
INFO: [Project 1-570] Preparing netlist for logic optimization

Processing XDC Constraints
Initializing timing engine
Parsing XDC File [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size_ooc.xdc]
create_clock: Time (s): cpu = 00:00:20 ; elapsed = 00:00:20 . Memory (MB): peak = 9801.188 ; gain = 0.000 ; free physical = 18249 ; free virtual = 323074
Finished Parsing XDC File [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size_ooc.xdc]
Parsing XDC File [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size_user.xdc]
Finished Parsing XDC File [/home/shcao/workspace/vecmul_size_kernels/vivado_rtl_kernel/vecmul_size_ex/imports/vecmul_size_user.xdc]
Completed Processing XDC Constraints

Netlist sorting complete. Time (s): cpu = 00:00:00.28 ; elapsed = 00:00:00.37 . Memory (MB): peak = 9801.188 ; gain = 0.000 ; free physical = 18247 ; free virtual = 323074
INFO: [Project 1-111] Unisim Transformation Summary:
No Unisim elements were transformed.

write_xdc: Time (s): cpu = 00:00:29 ; elapsed = 00:00:12 . Memory (MB): peak = 9801.223 ; gain = 0.000 ; free physical = 17892 ; free virtual = 323082
Constraint Validation Runtime : Time (s): cpu = 00:00:44 ; elapsed = 00:00:15 . Memory (MB): peak = 9801.223 ; gain = 0.000 ; free physical = 17799 ; free virtual = 323037
---------------------------------------------------------------------------------
Finished Constraint Validation : Time (s): cpu = 00:18:12 ; elapsed = 00:17:02 . Memory (MB): peak = 9801.223 ; gain = 7107.453 ; free physical = 15841 ; free virtual = 321781
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
Start Loading Part and Timing Information
---------------------------------------------------------------------------------
Loading part: xcu280-fsvh2892-2L-e
INFO: [Synth 8-6742] Reading net delay rules and data
---------------------------------------------------------------------------------
Finished Loading Part and Timing Information : Time (s): cpu = 00:18:12 ; elapsed = 00:17:02 . Memory (MB): peak = 9801.223 ; gain = 7107.453 ; free physical = 15840 ; free virtual = 321779
---------------------------------------------------------------------------------
INFO: [Synth 8-802] inferred FSM for state register 'wstate_reg' in module 'vecmul_size_control_s_axi'
INFO: [Synth 8-802] inferred FSM for state register 'rstate_reg' in module 'vecmul_size_control_s_axi'
INFO: [Synth 8-802] inferred FSM for state register 'state_reg_reg' in module 'MM2S'
INFO: [Synth 8-802] inferred FSM for state register 'state_reg_reg' in module 'S2MM'
INFO: [Synth 8-802] inferred FSM for state register 'state_r_reg' in module 'VitisRTLKernel'
---------------------------------------------------------------------------------------------------
                   State |                     New Encoding |                Previous Encoding 
---------------------------------------------------------------------------------------------------
                  iSTATE |                             0001 |                               11
*
                  WRIDLE |                             0010 |                               00
                  WRDATA |                             0100 |                               01
                  WRRESP |                             1000 |                               10
---------------------------------------------------------------------------------------------------
INFO: [Synth 8-3354] encoded FSM with state register 'wstate_reg' using encoding 'one-hot' in module 'vecmul_size_control_s_axi'
---------------------------------------------------------------------------------------------------
                   State |                     New Encoding |                Previous Encoding 
---------------------------------------------------------------------------------------------------
                  iSTATE |                              001 |                               10
*
                  RDIDLE |                              010 |                               00
                  RDDATA |                              100 |                               01
---------------------------------------------------------------------------------------------------
INFO: [Synth 8-3354] encoded FSM with state register 'rstate_reg' using encoding 'one-hot' in module 'vecmul_size_control_s_axi'
---------------------------------------------------------------------------------------------------
                   State |                     New Encoding |                Previous Encoding 
---------------------------------------------------------------------------------------------------
                 iSTATE3 |                             0110 |                             0000
                 iSTATE2 |                             0111 |                             0001
                 iSTATE1 |                             0101 |                             0010
                 iSTATE0 |                             0000 |                             0011
                  iSTATE |                             0001 |                             0100
                 iSTATE8 |                             0100 |                             0101
                 iSTATE6 |                             0011 |                             0110
                 iSTATE5 |                             1000 |                             0111
                 iSTATE7 |                             0010 |                             1000
                 iSTATE4 |                             1001 |                             1001
*
---------------------------------------------------------------------------------------------------
INFO: [Synth 8-3354] encoded FSM with state register 'state_reg_reg' using encoding 'sequential' in module 'MM2S'
---------------------------------------------------------------------------------------------------
                   State |                     New Encoding |                Previous Encoding 
---------------------------------------------------------------------------------------------------
                 iSTATE3 |                             0110 |                             0000
                 iSTATE2 |                             0101 |                             0001
                 iSTATE1 |                             0011 |                             0010
                  iSTATE |                             0000 |                             0011
                 iSTATE0 |                             0001 |                             0100
                 iSTATE8 |                             0010 |                             0101
                 iSTATE6 |                             0100 |                             0110
                 iSTATE5 |                             0111 |                             0111
                 iSTATE7 |                             1000 |                             1000
                 iSTATE4 |                             1001 |                             1001
*
---------------------------------------------------------------------------------------------------
INFO: [Synth 8-3354] encoded FSM with state register 'state_reg_reg' using encoding 'sequential' in module 'S2MM'
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_0_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_1_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_2_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_3_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_4_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_5_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_6_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_7_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_8_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_9_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_10_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_11_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_12_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_13_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_14_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
INFO: [Synth 8-6904] The RAM "Queue_2:/ram_15_reg" of size (depth=16 x width=32) is automatically implemented using LUTRAM. BRAM implementation would be inefficient 
---------------------------------------------------------------------------------------------------
                   State |                     New Encoding |                Previous Encoding 
---------------------------------------------------------------------------------------------------
                 iSTATE0 |                              000 |                              000
                 iSTATE1 |                              100 |                              001
                 iSTATE2 |                              011 |                              010
                 iSTATE3 |                              001 |                              011
                  iSTATE |                              010 |                              100
*
---------------------------------------------------------------------------------------------------
INFO: [Synth 8-3354] encoded FSM with state register 'state_r_reg' using encoding 'sequential' in module 'VitisRTLKernel'
---------------------------------------------------------------------------------
Finished RTL Optimization Phase 2 : Time (s): cpu = 00:28:38 ; elapsed = 00:27:53 . Memory (MB): peak = 9801.223 ; gain = 7107.453 ; free physical = 2236 ; free virtual = 308834
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
Start RTL Component Statistics 
---------------------------------------------------------------------------------
Detailed RTL Component Info : 
+---Adders : 
	   2 Input   96 Bit       Adders := 16    
	   3 Input   65 Bit       Adders := 1     
	   2 Input   64 Bit       Adders := 7     
	   3 Input   64 Bit       Adders := 2     
	   8 Input   32 Bit       Adders := 1     
	   2 Input   32 Bit       Adders := 23    
	   3 Input   32 Bit       Adders := 2     
	   2 Input    8 Bit       Adders := 3     
	   2 Input    7 Bit       Adders := 8     
	   3 Input    7 Bit       Adders := 4     
	   2 Input    5 Bit       Adders := 2     
	   2 Input    4 Bit       Adders := 3     
	   2 Input    2 Bit       Adders := 1     
+---XORs : 
	   2 Input      1 Bit         XORs := 2     
+---Registers : 
	               64 Bit    Registers := 3     
	               32 Bit    Registers := 5152  
	                7 Bit    Registers := 4     
	                6 Bit    Registers := 1     
	                5 Bit    Registers := 2     
	                4 Bit    Registers := 2     
	                2 Bit    Registers := 2     
	                1 Bit    Registers := 17    
+---Multipliers : 
	              32x64  Multipliers := 1     
	              32x32  Multipliers := 8     
+---RAMs : 
	              48K Bit	(96 X 512 bit)          RAMs := 2     
	              512 Bit	(16 X 32 bit)          RAMs := 16    
+---Muxes : 
	   2 Input  512 Bit        Muxes := 2     
	   6 Input  512 Bit        Muxes := 1     
	   2 Input   64 Bit        Muxes := 3     
	   8 Input   64 Bit        Muxes := 1     
	   2 Input   32 Bit        Muxes := 30798 
	   6 Input   32 Bit        Muxes := 1024  
	   3 Input   32 Bit        Muxes := 3     
	   4 Input   32 Bit        Muxes := 2     
	   9 Input   32 Bit        Muxes := 1     
	  10 Input   32 Bit        Muxes := 1     
	   2 Input    8 Bit        Muxes := 2     
	   8 Input    8 Bit        Muxes := 1     
	   5 Input    8 Bit        Muxes := 1     
	   2 Input    7 Bit        Muxes := 9     
	   3 Input    7 Bit        Muxes := 2     
	   5 Input    7 Bit        Muxes := 1     
	   2 Input    5 Bit        Muxes := 3     
	   4 Input    4 Bit        Muxes := 1     
	   2 Input    4 Bit        Muxes := 13    
	  10 Input    4 Bit        Muxes := 2     
	   3 Input    3 Bit        Muxes := 1     
	   2 Input    3 Bit        Muxes := 4     
	   4 Input    3 Bit        Muxes := 3     
	   5 Input    3 Bit        Muxes := 1     
	   2 Input    2 Bit        Muxes := 2     
	   2 Input    1 Bit        Muxes := 33839 
	   4 Input    1 Bit        Muxes := 10    
	   3 Input    1 Bit        Muxes := 1     
	  11 Input    1 Bit        Muxes := 2     
	   7 Input    1 Bit        Muxes := 1     
	   8 Input    1 Bit        Muxes := 2     
	   6 Input    1 Bit        Muxes := 3     
	   9 Input    1 Bit        Muxes := 2     
	   5 Input    1 Bit        Muxes := 1     
---------------------------------------------------------------------------------
Finished RTL Component Statistics 
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
Start Part Resource Summary
---------------------------------------------------------------------------------
Part Resources:
DSPs: 9024 (col length:94)
BRAMs: 4032 (col length: RAMB18 288 RAMB36 144)
---------------------------------------------------------------------------------
Finished Part Resource Summary
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
Start Cross Boundary and Area Optimization
---------------------------------------------------------------------------------
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[63] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[62] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[61] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[60] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[59] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[58] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[57] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[56] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[55] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[54] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[53] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[52] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[51] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[50] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[49] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[48] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[47] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[46] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[45] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[44] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[43] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[42] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[41] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[40] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[39] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[38] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[37] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[36] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[35] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[34] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[33] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[32] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[31] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[30] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[29] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[28] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[27] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[26] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[25] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[24] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[23] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[22] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[21] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[20] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[19] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[18] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[17] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[16] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[15] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[14] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[13] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[12] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[11] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[10] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[9] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[8] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[7] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[6] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[5] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[4] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[3] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[2] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[1] driven by constant 1
WARNING: [Synth 8-3917] design vecmul_size has port m00_axi_wstrb[0] driven by constant 1
WARNING: [Synth 8-3332] Sequential element (FSM_onehot_wstate_reg[0]) is unused and will be removed from module vecmul_size_control_s_axi.
WARNING: [Synth 8-3332] Sequential element (FSM_onehot_rstate_reg[0]) is unused and will be removed from module vecmul_size_control_s_axi.

