### 使用单例 管理 DaoSession

可以全局保留唯一一个DaoSession对象。

各Activity都可以调用这个对象，避免了创建多个对象的开销。

#### 首先弄出GreenDao管理者

```java
public class GreenDaoManager {
    private static final String TAG = "GreenDaoManager";

    private static final String DATABASE_NAME = "name.db";
    
    /**
     * 全局保持一个DaoSession
     */
    private DaoSession daoSession;

    private boolean isInited;

    private static final class GreenDaoManagerHolder {
        private static final GreenDaoManager sInstance = new GreenDaoManager();
    }

    public static GreenDaoManager getInstance() {
        return GreenDaoManagerHolder.sInstance;
    }

    private GreenDaoManager() {

    }

    /**
     * 初始化DaoSession
     *
     * @param context
     */
    public void init(Context context) {
        if (!isInited) {
            DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(
                    context.getApplicationContext(), DATABASE_NAME, null);
            DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
            daoSession = daoMaster.newSession();
            isInited = true;
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
```

#### 在Application的onCreate()方法中进行初始化

```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        GreenDaoManager.getInstance().init(getApplicationContext());
    }
}
```

#### 在AndroidManifest.xml中更改Application的name：

```
    <application
        android:name=".MyApplication"
        // 其他代码...
    </application>
```

#### 接下来，在Activity使用单例DaoSession

```
    /**
     * 获取一个全局的DaoSession实例
     */
    private void initDaoSession() {
        mDaoSession = GreenDaoManager.getInstance().getDaoSession();
    }
```

