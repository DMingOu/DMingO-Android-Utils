package com.example.taxidata.util;

import com.example.taxidata.common.eventbus.BaseEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author: ODM
 * @date: 2019/8/11
 */
public class EventBusUtils {


        /**
         * 取消注册EventBus
         *
         * @param subscriber the subscriber
         */
        public static void unregister(Object subscriber) {
            if (EventBus.getDefault().isRegistered(subscriber)) {
                EventBus.getDefault().unregister(subscriber);
            }
        }

        /**
         * 注册EventBus
         *
         * @param subscriber the subscriber
         */
        public static void register(Object subscriber) {
            EventBus.getDefault().register(subscriber);
        }

        /**
         * 将事件发送出去
         *
         * @param baseEvent the base event
         */
        public static void post(BaseEvent baseEvent) {
            EventBus.getDefault().post(baseEvent);
        }

        /**
         * 将粘性事件发送出去
         *
         * @param baseEvent the base event
         */
        public static void postSticky(BaseEvent baseEvent) {
            EventBus.getDefault().postSticky(baseEvent);
        }
    }

