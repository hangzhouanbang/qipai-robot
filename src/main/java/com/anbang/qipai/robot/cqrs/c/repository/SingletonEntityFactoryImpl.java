package com.anbang.qipai.robot.cqrs.c.repository;

import com.highto.framework.ddd.SingletonEntityFactory;

public class SingletonEntityFactoryImpl implements SingletonEntityFactory {

	@Override
	public <T> T createNew(Class<T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
