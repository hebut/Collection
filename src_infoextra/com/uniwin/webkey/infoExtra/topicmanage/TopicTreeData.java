package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import com.uniwin.webkey.infoExtra.itf.PersonTopicService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;

public class TopicTreeData extends AbstractTreeModel {

	PersonTopicService personTopicService;
	private Integer userId;
	public TopicTreeData(Object root,PersonTopicService personTopicService,Integer userId) {
		super(root);
		this.personTopicService = personTopicService;
		this.userId = userId;
	}

	private static final long serialVersionUID = 1L;

	public Object getChild(Object parent, int index) {
		if(parent instanceof WKTPersonTopic){
			WKTPersonTopic w = (WKTPersonTopic)parent;
			List<WKTPersonTopic> clist = personTopicService.findByPidAndUserId(w.getKptId(), userId);
			return clist.get(index);	
		}
		else{
			if(parent instanceof List){
				List s= (List)parent;
				return s.get(index);
			}
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if(parent instanceof WKTPersonTopic){
			WKTPersonTopic w = (WKTPersonTopic)parent;
			List<WKTPersonTopic> clist = personTopicService.findByPidAndUserId(w.getKptId(), userId);
			return clist.size();	
		}
		else{
			if(parent instanceof List){
				List s= (List)parent;
				return s.size();
			}
		}
		return 0;
	}

	public boolean isLeaf(Object node) {
		if(node instanceof WKTPersonTopic){
			WKTPersonTopic w = (WKTPersonTopic)node;
			List<WKTPersonTopic> clist = personTopicService.findByPidAndUserId(w.getKptId(), userId);
			return clist.size()>0?false:true;	
		}
		else{
			if(node instanceof List){
				List s= (List)node;
				return s.size()>0?false:true;
			}
		}
		return true;
	}

}
