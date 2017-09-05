package com.hadoop.test.db;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ConnMysqlReducer1 extends Reducer< Text,Text,UserRecord,UserRecord>
{  
    public void reduce(Text key,Iterable< Text> values,Context context)throws IOException,InterruptedException
    {  
    //接收到的key value对即为要输入数据库的字段，所以在reduce中：
    //wirte的第一个参数，类型是自定义类型UserRecord，利用key和value将其组合成UserRecord，然后等待写入数据库
    //wirte的第二个参数，wirte的第一个参数已经涵盖了要输出的类型，所以第二个类型没有用，设为null
    for(Iterator< Text> itr = values.iterator();itr.hasNext();)
             {  
                 context.write(new UserRecord(key.toString(),itr.next().toString()),null);
             }  
    }  
}
