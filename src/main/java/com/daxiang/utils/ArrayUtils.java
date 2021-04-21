package com.daxiang.utils;

/*
 * 实现动态数组：
 * 1.数组缺陷：(1)数组长度一经定义不能再被修改；(2)同一数组中数据类型只能一致
 * 2.解决：(1)arrayCode()//数组拷贝——扩容；(2)Object可以存储任意类型
 */
public class ArrayUtils {
    private Object[] data;//存储数据的数组
    private int size;//真正存储的个数

    public ArrayUtils() {
        this.data = new Object[10];
        this.size = 0;
    }

    public int getSize() {
        return size;
    }
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0 ; i< size; i++) {
            sb.append(data[i]);
        }
        return sb.toString();
    }

    public boolean isEmpty() {//判断该动态数组是否为空
        return this.size == 0;
    }

    public boolean add(Object data) {//向数组添加数据
        if (this.size == this.data.length) {//扩容
            Object[] newArray = new Object[this.data.length * 2];//空间扩大
            System.arraycopy(this.data, 0, newArray, 0, this.data.length);//数据复制
            this.data = newArray;//将data指向新的数组
        }
        this.data[size++] = data;
        return true;
    }

    public Object get(int index) {//按位取值
        if (index < 0 || index > size) {
            return null;
        }
        return this.data[index];
    }

    public boolean add_index(Object data, int index) {//向数组指定位置添加数据
        if (this.size == this.data.length) {//扩容
            Object[] newArray = new Object[this.data.length * 2];//空间扩大
            System.arraycopy(this.data, 0, newArray, 0, this.data.length);//数据复制
            this.data = newArray;//将data指向新的数组
        }
        if (index < 0 || index > size) {
            return false;
        }
        System.arraycopy(this.data, index - 1, this.data, index, this.data.length - index);
        this.data[index - 1] = data;
        size++;
        return true;
    }

    public boolean remove(int index) {//删除指定位置元素
        if (this.size < this.data.length / 2) {//缩容
            Object[] newArray = new Object[this.data.length / 2];
            System.arraycopy(this.data, 0, newArray, 0, this.data.length);//数据复制
            this.data = newArray;//将data指向新的数组
        }
        if (index < 0 || index > size) {
            return false;
        }
        System.arraycopy(this.data, index, this.data, index - 1, this.data.length - index);
        size--;
        return true;
    }
}