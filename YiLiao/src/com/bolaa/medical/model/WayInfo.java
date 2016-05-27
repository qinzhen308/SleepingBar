package com.bolaa.medical.model;

import java.io.Serializable;

public class WayInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String payment_id;
	private String payment_code;
	private String payment_name;
	private String payment_state;
	private Payment_config payment_config;

	public class Payment_config {
		private String alipay_service;
		private String alipay_account;
		private String alipay_key;
		private String alipay_partner;
		private String cmb_account;
		private String cmb_partner;
		private String cmb_key;
		private String discount;
		private String unionpay_account;
		private String unionpay_key;

		public String getAlipay_service() {
			return alipay_service;
		}

		public void setAlipay_service(String alipay_service) {
			this.alipay_service = alipay_service;
		}

		public String getAlipay_account() {
			return alipay_account;
		}

		public void setAlipay_account(String alipay_account) {
			this.alipay_account = alipay_account;
		}

		public String getAlipay_key() {
			return alipay_key;
		}

		public void setAlipay_key(String alipay_key) {
			this.alipay_key = alipay_key;
		}

		public String getAlipay_partner() {
			return alipay_partner;
		}

		public void setAlipay_partner(String alipay_partner) {
			this.alipay_partner = alipay_partner;
		}

		public String getCmb_account() {
			return cmb_account;
		}

		public void setCmb_account(String cmb_account) {
			this.cmb_account = cmb_account;
		}

		public String getCmb_partner() {
			return cmb_partner;
		}

		public void setCmb_partner(String cmb_partner) {
			this.cmb_partner = cmb_partner;
		}

		public String getCmb_key() {
			return cmb_key;
		}

		public void setCmb_key(String cmb_key) {
			this.cmb_key = cmb_key;
		}

		public String getDiscount() {
			return discount;
		}

		public void setDiscount(String discount) {
			this.discount = discount;
		}

		public String getUnionpay_account() {
			return unionpay_account;
		}

		public void setUnionpay_account(String unionpay_account) {
			this.unionpay_account = unionpay_account;
		}

		public String getUnionpay_key() {
			return unionpay_key;
		}

		public void setUnionpay_key(String unionpay_key) {
			this.unionpay_key = unionpay_key;
		}

	}

	public String getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(String payment_id) {
		this.payment_id = payment_id;
	}

	public String getPayment_code() {
		return payment_code;
	}

	public void setPayment_code(String payment_code) {
		this.payment_code = payment_code;
	}

	public String getPayment_name() {
		return payment_name;
	}

	public void setPayment_name(String payment_name) {
		this.payment_name = payment_name;
	}

	public Payment_config getPayment_config() {
		return payment_config;
	}

	public void setPayment_config(Payment_config payment_config) {
		this.payment_config = payment_config;
	}

	public String getPayment_state() {
		return payment_state;
	}

	public void setPayment_state(String payment_state) {
		this.payment_state = payment_state;
	}

}
