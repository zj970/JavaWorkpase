package base;

/**
 * @author 周建
 * @version  1.0
 * @since jdk1.8
 */
public class Doc {

    String name;


    /**
     * @author zj970 作者
     * @param name 参数
     * @return  String 返回值
     * @throws Exception
     */
    public String test(String name) throws Exception{
        return name;
    }

    //生成文档 javadoc -encoding UTF-8 -charset UTF-8 xxx.java
}
