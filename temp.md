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