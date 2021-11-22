package net.cz.blog.services;

import net.cz.blog.Response.ResponseResult;
import net.cz.blog.pojo.Looper;

public interface ILooperService {
    ResponseResult addLooper(Looper looper);

    ResponseResult getLooperList(int page, int size);

    ResponseResult getLooper(String looperId);

    ResponseResult deleteLooper(String looperId);

    ResponseResult updateLooper(String looperId, Looper looper);
}
