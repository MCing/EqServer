package com.eqsys.util;

/** 服务端控制子命令 */
public enum SubCommand {

	TRAMS_MODE((short) 1), // 传输模式控制
	PERIOD_DATA_REQ((short) 2), // 时间段数据申请
	THRESHOLD_SETTING((short) 3), // 触发阈值设定控制
	TRIGGER_SETTING((short) 4); // 时间段触发控制

	private short value;

	private SubCommand(short value) {
		this.value = value;
	}

	public short value() {
		return this.value;
	}
}
