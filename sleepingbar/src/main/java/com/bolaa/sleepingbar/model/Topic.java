package com.bolaa.sleepingbar.model;

import java.util.List;

/**
 * 社区首页--话题
 * Created by paulz on 2016/6/14.
 */
public class Topic {
    public String avatar;
    public String c_time;
    public int comment_num;
    public String content;
    public String id;
    public String img_path;
    public String nick_name;
    public int praise_num;
    public int is_praise;
    public int status;
    public List<String> topic_imgs;
    public String user_id;
    public int has_been_cared;//该贴主人是否已被我关注

}
