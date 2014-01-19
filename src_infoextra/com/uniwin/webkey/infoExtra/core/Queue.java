package com.uniwin.webkey.infoExtra.core;
/**
 * 存储链接表
 */
import java.util.LinkedList;

public class Queue<T> {

	private  LinkedList<T> queue=new LinkedList<T>(); 
	
	public synchronized void enQueue(T t){
		queue.addLast(t);
	}
	
	public synchronized T delQueue(){
		if (queue!=null && queue.size()>0)
		return queue.removeFirst();
		else return null;
	}
	
	public synchronized boolean isQueueEmpty(){
		return queue.isEmpty();
	}
	
	public boolean contains(T t){
		return queue.contains(t);
	}
	
	public int queueLength(){
		return queue.size();
	}
	public synchronized void deleteAll(){
		queue.clear();
	}
	
}
