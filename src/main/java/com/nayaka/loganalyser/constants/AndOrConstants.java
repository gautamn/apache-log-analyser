package com.andor.misc.constants;

public interface AndOrConstants {

	public static final String RESPONSE_STATUS_OK = "1";
	public static final String RESPONSE_STATUS_ERROR = "0";

	public static final String DB_INGESTION_SUCCESS = "SUCCESS";
	public static final String DB_INGESTION_FAILURE = "FAILURE";

	public static final String DB_UPDATION_FAILURE = "FAILURE";
	public static final String DB_DUPLICASY_FAILURE = "DUPLICATE ENTRY";

	public static final String UNIVERSAL_CHARSET = "UTF-8";
	public static final String SORT_ASC = "ASC";
	public static final String SORT_DESC = "DESC";

	public final String append = "&";
	public final int SUBSCRIBE_EMAIL_FLAG = 1;
	public final int UNSUBSCRIBE_EMAIL_FLAG = 0;
	
	public final String N = "N";
	public final String Y = "Y";
	
	public static final String DEL = "|";
	
	public static final int EMAIL_VERIFIED = 1;
	public static final int EMAIL_NOT_VERIFIED = 0;
	
	public static final int MOBILE_VERIFIED = 1;
	public static final int MOBILE_NOT_VERIFIED = 0;
	
	public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";

	// request header fields
	public static final String REQ_HEAD_AUTHORIZATION_HEADER = "authorization";
	
	public static final String RESPONSE_HASH = "responseHash";
	
	public static final String DEFAULT_LANGUAGE = "en";
	
	public static final String API_HEADER = "apiHeader";
	public static final String CONTENT_TYPE_TEXT = "text";
}