package Demo09;
//一个类可以实现接口：关键字 implements
//接口可以多继承
//实现了接口的类，就需要重写接口的方法
//抽象的思维
public class UserServiceImpl implements UserService,TimeService{
    @Override
    public void add(String name) {

    }

    @Override
    public void delete(String name) {

    }

    @Override
    public void update(String name) {

    }

    @Override
    public void query(String name) {

    }

    @Override
    public void timer() {

    }
}

/**
 *
 作用：

 1. 约束

 2. 定义一些方法，让不同的人实现

 3. public abstract

 4. public static final

 5. 接口不能被实例化，接口中没有构造方法

 6. implements可以实现多个接口

 7. 必须要重写接口中的方法

 接口与抽象类的异同

 相同：
 都可被继承，都规范方法
 不同：
 抽象单继承，接口多继承
 抽象有构造，接口无构造
 抽象可具体，接口只规范

 */


