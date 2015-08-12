package com.jingzufang.bean;

public class UserEntity {
	private String id;
	private String passwd;//密码
	private String sjh;//手机号码
	private String gerden;//性别
	private String flag;//类型
	private String name;//用户名
	private String tx;
	public UserEntity(String id, String passwd, String sjh, String name, String gerden, String flag, String tx){
		this.id = id;
		this.passwd = passwd;
		this.sjh = sjh;
		this.name = name;
		this.gerden = gerden;
		this.flag = flag;
		this.tx = tx;
	}
	public String getId() {
		return id;
	}
	public String getPasswd() {
		return passwd;
	}
	public String getSjh() {
		return sjh;
	}
	public String getGerden() {
		return gerden;
	}
	public String getFlag() {
		return flag;
	}
	public String getName() {
		return name;
	}
	public String getTx() {
		return tx;
	}
	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", passwd=" + passwd + ", sjh=" + sjh
				+ ", gerden=" + gerden + ", flag=" + flag + ", name=" + name
				+ ", tx=" + tx + "]";
	}
	
}
