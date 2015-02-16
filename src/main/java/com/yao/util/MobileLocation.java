package com.yao.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.ByteArrayBuffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;

public class MobileLocation {
		public static void main(String[] args){
		
		String mobile = "15102539865";

		 try {
			 if(isMobile(mobile)){
				String url = "http://www.baidu.com/s?wd="+mobile;
				
				String bodyContent = getBodyContent(url);
				
				if(!"".equals(bodyContent)){
					
					String mobileInfo = parseResponseContent(bodyContent);
					
					System.out.println("mobileInfo = " + mobileInfo);
				}
				
			 }else{
				 System.out.println("not a mobile");
			 }

		}catch(Exception ex){
			ex.printStackTrace();
		} 
		 
		 
	}
	/**
	 * 
	 * <p>������������ȡget����ķ�����Ϣ</p>
	 * <p>�������ڣ�2015��2��16��</p>
	 * @return String
	 * @author jason
	 * @update [�������� yyyy-MM-dd] [����������]
	 */
	public static String getBodyContent(String url){
		String bodyContent = "";

		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		CloseableHttpResponse response = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		
		try{
			HttpGet httpGet = new HttpGet(url);
			response = httpclient.execute(httpGet);
			
			System.out.println(response.getStatusLine());
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				
				if(entity != null){
					is = entity.getContent();
					bis = new BufferedInputStream(is); 
					
					ByteArrayBuffer baf = new ByteArrayBuffer(20); 
					
					int current = 0; 
					
					while((current = bis.read()) != -1){ 
						baf.append((byte)current); 
					} 
					
					bodyContent = new String(baf.toByteArray(),"utf-8");
					
				}
			}
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
		    try {
		    	bis.close();
				is.close();
				httpclient.close();
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		
		return bodyContent;
	}
	/**
	 * 
	 * <p>�����������������󷵻���body���ݣ��������ֻ�����ʡ�ݺͳ��У������json��ʽ��������</p>
	 * <p>�������ڣ�2015��2��16��</p>
	 * @return String
	 * @author jason
	 * @update [�������� yyyy-MM-dd] [����������]
	 */
	public static String parseResponseContent(String bodyContent) throws Exception{
		String mobileInfo = "";
		Document doc = Jsoup.parse(bodyContent);  
		Elements elements = doc.getElementsByClass("op_mobilephone_r");
		
		for(Element element : elements){
			String phoneStr = element.child(0).html();
			String addressStr = element.child(1).html();
			
			String phoneNum = phoneStr.substring(10, 21);
			
			String provice = "";
			String city = "";
			
			int flag2 = addressStr.indexOf("&nbsp;&nbsp;");
			
			int flag3 = addressStr.indexOf("&nbsp;");
			
			String tMobile = addressStr.substring(flag2+12, addressStr.length());
			city = addressStr.substring(flag3+6, flag2);
			
			if(addressStr.startsWith("&nbsp;")){
				provice = city;
			}else{
				provice = addressStr.substring(0, flag3);
			}
			
			Map<String,String> mobileMap = new HashMap<String,String>();
			mobileMap.put("phoneNum", phoneNum);   //�绰����
			mobileMap.put("tMobile", tMobile);     //��Ӫ��
			mobileMap.put("provice", provice);     //ʡ��
			mobileMap.put("city", city);           //����
			
			mobileInfo = JSON.toJSONString(mobileMap);
		}
		
		return mobileInfo;
	}
	/**
	 * 
	 * <p>������������֤�ֻ������Ƿ���Ч</p>
	 * <p>�������ڣ�2015��2��16��</p>
	 * @return boolean
	 * @author jason
	 * @update [�������� yyyy-MM-dd] [����������]
	 */
	public static boolean isMobile(String str) throws Exception{
		
		if(str == null){
			return false;
		}else{
			Pattern p = null;
			Matcher m = null;
			boolean b = false; 
			p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$");
			m = p.matcher(str);
			b = m.matches(); 
			return b;
		}
		
	} 
}
