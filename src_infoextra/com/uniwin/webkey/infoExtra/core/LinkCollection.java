package com.uniwin.webkey.infoExtra.core;
/**
 *type 0 提取的起始地址
 * type 1 提取的下一页网址
 * type 2 采集的符合模板的网址
 * type 3 其它情况
 * 已访问和未访问链接的队列
 */
import java.util.HashSet;
import java.util.Set;

public class LinkCollection {

	private static Set<String> visitedUrl=new HashSet<String>();

	public synchronized static Set<String> getVisitedUrl() {
		return visitedUrl;
	}
	public synchronized static void setVisitedUrl(Set<String> visitedUrl) {
		LinkCollection.visitedUrl = visitedUrl;
	}
	
	private static Queue<String> unVisitedUrl=new Queue<String>();

	public synchronized static  Queue<String> getUnVisitedUrl() {
		return unVisitedUrl;
	}
	public static synchronized void setUnVisitedUrl(Queue<String> unVisitedUrl) {
		LinkCollection.unVisitedUrl = unVisitedUrl;
	}
	
	public synchronized static  void addVisitedUrl(String url){
		visitedUrl.add(url);
	}
	
	public synchronized static  void removeVisitedUrl(String url){
		visitedUrl.remove(url);
	}
	
	public synchronized static  String unVisitedUrlDeQueue() {
		return unVisitedUrl.delQueue();
	}
	
	//每个url保证访问一次
	public synchronized static void addUnvisitedUrl(String url,Integer type) {
		
		
		if (url != null && !url.trim().equals("")
				&& !visitedUrl.contains(url) && !unVisitedUrl.contains(url)){
			
			if(type==0){
				System.out.println("起始地址："+url);
			}else if(type==1){
				System.out.println("提取下一页成功！");
			}else if(type==2){
//				System.out.println("提取的网址："+url);
			}
			unVisitedUrl.enQueue(url);
		}
		
		
		
	}
	
	public static int getVisitedUrlNum() {
		return visitedUrl.size();
	}
	
	public static int getunVisitedUrlNum(){
		return unVisitedUrl.queueLength();
	}
	
	public synchronized static boolean unVisitedUrlsEmpty() {
		return unVisitedUrl.isQueueEmpty();
	}
	
}
