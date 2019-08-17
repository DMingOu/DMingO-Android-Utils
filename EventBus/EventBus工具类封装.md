### 定义 事件基类  BaseEvent

BaseEvent.java

```
public class BaseEvent {
    public String type;
    public String content;
    public Object object;
}
```



## 单例化 BaseEvent

EventFactory.java

```
public class EventFactory {
    public static BaseEvent getInstance() {
        return new BaseEvent();
    }
}
```



##  封装EventBus 常用的方法 

EventBusUtils.java

```
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
```



## 接收EventBus 事件

需要在哪里接收 ，就要去哪里 注册订阅EventBus ，在销毁时要解除注册

可以考虑在 BaseView ，BaseAvtivity ，BaseFragment 等地方进行初始化注册

让子类可以安心重写 handleEvent方法处理收到的事件

```
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册eventbus
        if (isRegisterEventBus()) {
            EventBusUtils.register(this);
        }
    }

@Override
    public void onDestroy() {
        super.onDestroy();
        //...
        if (isRegisterEventBus()) {
        
          EventBusUtils.unregister(this);
        }
    }
   
   //注册绑定EventBus
    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }
    
    /**
     * 处理eventbus发过来的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(BaseEvent baseEvent) {
    }
```



## 发送EventBus 事件

在任何地方都可以发送 BaseEvent事件

eg:

```
BaseEvent baseEvent = EventFactory.getInstance();
baseEvent.type = Constant.STATUS;
baseEvent.content = Constant.SUCCESS;
EventBusUtils.post(baseEvent);
```

在 接收方的 handleEvent 方法只要对 baseEvent 的 类型进行判断就可以对 内容 content 进行处理了。

粘性事件，原理相同，只是粘性事件可以保证让接收方必定能接收，不用担心初始化注册的问题



### 注意

当 Activity1 和 Activity 2 当用  `@Subscribe(threadMode = ThreadMode.MAIN)`

 标注接收EventBus事件时，如果 Activity1 是 采用了 `android:launchMode="singleTask"`

会导致 Activity 2 跳转到 Activity 1时，Activity2 会执行 onDestory 。而且 Activity2 无法接收 来自另外一个栈的 Activity1 的 EventBus 消息。

如果不想让 Activity 的 event 在 onCreate 前收到，可以尝试将 Activity 的启动模式设置为 `singInstance`