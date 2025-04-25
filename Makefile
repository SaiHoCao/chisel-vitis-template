CHISEL_BUILD_DIR = ./build/chisel

############################## Chisel Flow #############################
test:
	./mill -i __.test.testOnly vitisrtlkernel.VitisRTLKernelTest

verilog:
	mkdir -p $(CHISEL_BUILD_DIR)
	./mill -i chiselVitisTemplate.runMain --mainClass vitisrtlkernel.VitisRTLKernelVerilog -td $(CHISEL_BUILD_DIR)

help:
	mill -i __.runMain --mainClass vitisrtlkernel.VitisRTLKernelVerilog --help

compile:
	mill -i __.compile

bsp:
	mill -i mill.bsp.BSP/install

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

clean:
	-rm -rf $(CHISEL_BUILD_DIR)

.PHONY: test verilog help compile bsp reformat checkformat clean

############################## XCLBIN Flow #############################

XCLBIN_BUILD_DIR = ./build/xclbin

XCLBIN_TEMP_DIR = $(XCLBIN_BUILD_DIR)/tmp
XCLBIN_LOG_DIR = $(XCLBIN_BUILD_DIR)/log 
XCLBIN_REPORT_DIR = $(XCLBIN_BUILD_DIR)/report

VPP = v++
KERNEL_XO = ./xo_kernel/$(XO).xo
LINK_CFG = ./xo_kernel/$(XO).cfg



xclbin: $(KERNEL_XO) $(LINK_CFG)
	mkdir -p $(XCLBIN_TEMP_DIR)
	mkdir -p $(XCLBIN_LOG_DIR)
	mkdir -p $(XCLBIN_REPORT_DIR)
	$(VPP) -t hw \
	--temp_dir $(XCLBIN_TEMP_DIR) --save-temps --log_dir $(XCLBIN_LOG_DIR) --report_dir $(XCLBIN_REPORT_DIR) \
	--link $(KERNEL_XO) \
	--config $(LINK_CFG) -o ./xo_kernel/$(XO).xclbin

xclbin_emu: $(KERNEL_XO) $(LINK_CFG)
	mkdir -p $(XCLBIN_TEMP_DIR)
	mkdir -p $(XCLBIN_LOG_DIR)
	mkdir -p $(XCLBIN_REPORT_DIR)
	$(VPP) -t hw_emu \
	--temp_dir $(XCLBIN_TEMP_DIR) --save-temps --log_dir $(XCLBIN_LOG_DIR) --report_dir $(XCLBIN_REPORT_DIR) \
	--link $(KERNEL_XO) \
	--config $(LINK_CFG) -o ./xo_kernel/$(XO).xclbin

clean_vpp :
	-rm -rf $(XCLBIN_TEMP_DIR)
	-rm -rf $(XCLBIN_LOG_DIR)
	-rm -rf $(XCLBIN_REPORT_DIR)
	-rm -rf ./.ipcaches

.PHONY: xclbin clean_vpp 

############################## Host Flow #############################

HOST_BUILD_DIR = ./build/host

HOST_SRC = ./host/*.cpp
HOST_INCLUDE = ./host/include

HOST_EXECUTABLE = $(HOST_BUILD_DIR)/host_executable

CXX := g++
CXXFLAGS += -g -std=c++17 -Wall
LDFLAGS += -I$(HOST_INCLUDE) -I$(XILINX_XRT)/include -L$(XILINX_XRT)/lib -lxrt_coreutil -pthread

host: $(HOST_SRC)
	mkdir -p $(HOST_BUILD_DIR)
	$(CXX) $(CXXFLAGS) $(HOST_SRC) -o $(HOST_EXECUTABLE) $(LDFLAGS)

run: host $(HOST_EXECUTABLE)
	$(HOST_EXECUTABLE) ./xo_kernel/$(XCLBIN).xclbin

run_emu: host $(HOST_EXECUTABLE)
	XCL_EMULATION_MODE=hw_emu $(HOST_EXECUTABLE) ./xo_kernel/$(XCLBIN).xclbin

DEV_XVC_PUB := /dev/xvc_pub.u0
hw_debug:
	xvc_pcie -d $(DEV_XVC_PUB)

.PHONY: host run hw_debug