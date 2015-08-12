package com.xiangmu.tenement;

import android.os.Parcel;
import android.os.Parcelable;

public class TenementBean implements Parcelable {

	public int fw_id;// 房屋id
	public int fbr_id;// 发布人id
	public int loupanicon;// 地址图标
	public String title;// 房间描述
	public String fwdz;// 租房地址
	public String xqm;//小区名
	public String hx;// 房间户型。几室几厅
	public String czlx;// 租房类型。合租还是单组
	public double czjg;// 租房单价
	public String photos_dir;//房屋照片文件夹

	public TenementBean() {
	}

	public TenementBean(int fw_id, int loupanicon, String title, String fwdi, String xqm, String czlx, String hx, Double czjg,String photos_dir) {
		super();
		this.fw_id = fw_id;
		this.loupanicon = loupanicon;
		this.title = title;
		this.fwdz = fwdi;
		this.xqm = xqm;
		this.czlx = czlx;
		this.hx = hx;
		this.czjg = czjg;
		this.photos_dir=photos_dir;
	}

	public TenementBean(Parcel parcel) {
		fw_id = parcel.readInt();
		loupanicon = parcel.readInt();
		title = parcel.readString();
		fwdz = parcel.readString();
		xqm = parcel.readString();
		czlx = parcel.readString();
		hx = parcel.readString();
		czjg = parcel.readDouble();
		photos_dir=parcel.readString();
	}

	@Override
	public String toString() {
		return "TenementBean [房屋id=" + fw_id + ", 房屋略缩图=" + loupanicon
				+ ", 简介=" + title + ", 房屋地址=" + fwdz + ", 小区名=" + xqm
				+ ", 出租类型=" + czlx + ", 户型=" + hx + ", 出租价格="
				+ czjg + " ]";
	}

	// 内容描述接口，基本不管用
	public int describeContents() {
		return 0;
	}

	// 写入接口函数，打包
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(fw_id);
		dest.writeInt(loupanicon);
		dest.writeString(title);
		dest.writeString(fwdz);
		dest.writeString(xqm);
		dest.writeString(czlx);
		dest.writeString(hx);
		dest.writeDouble(czjg);
		dest.writeString(photos_dir);
	}

	public static Parcelable.Creator<TenementBean> CREATOR = new Creator<TenementBean>() {

		public TenementBean createFromParcel(Parcel source) {
			return new TenementBean(source);
		}

		public TenementBean[] newArray(int size) {
			return new TenementBean[size];
		}
	};

}
