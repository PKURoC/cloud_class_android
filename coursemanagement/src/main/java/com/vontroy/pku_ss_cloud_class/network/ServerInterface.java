package com.vontroy.pku_ss_cloud_class.network;

/**
 * Created by LinkedME06 on 16/10/29.
 */

public class ServerInterface {

    public static final String VERSION_INFO_URL = "http://www.vontroy.com/appinfo/update.json";

    public static final String baseUrl = "http://180.163.236.3/s/";
    public static final String baseABEUrl = "http://180.163.236.3/abe/abe_service/";
    public static final String uploadBaseUrl = "http://180.163.236.3/s/";
//    public static final String baseUrl = "http://192.168.99.184:8080/s/";
//    public static final String uploadBaseUrl = "http://192.168.99.184:8080/s/";
//    public static final String baseABEUrl = "http://192.168.200.125:8080/abe_service/";

    /**
     * 完整性检测
     * post
     */
    public static final String integrityCheckUrl = baseUrl + "integrity/check";

    /**
     * 完整性-修复
     * post
     */
    public static final String integrityRecoverUrl = baseUrl + "integrity/recover";

    /**
     * 获取ABE公共参数
     * get
     */
    public static final String getPKUrl = baseABEUrl + "get_pk";

    /**
     * 获取私钥
     * post
     */
    public static final String getSKUrl = baseABEUrl + "get_sk";
//    public static final String baseUrl = "http://172.16.0.105:8080/AWS/s/";

    /**
     * 获取随机数
     * get
     */
    public static final String rnd = baseUrl + "passport/getrnd";

    /**
     * 邮箱验证
     * get
     */
    public static final String verifyReg = baseUrl + "passport/verifyReg";

    /**
     * 注册接口
     * post
     */
    public static final String reg = baseUrl + "passport/reg";

    /**
     * 登录接口
     * post
     */
    public static final String login = baseUrl + "passport/login";

    /**
     * 重置密码
     * post
     * params: sid, token, password, newpassword
     */
    public static final String resetPwd = baseUrl + "passport/newpsw";

    /**
     * 忘记密码
     * post
     */
    public static final String forgotPwd = baseUrl + "passport/forgotpsw";

    /**
     * 退出登录
     * get
     */
    public static final String logout = baseUrl + "passport/logout";

    /**
     * 修改昵称
     * post
     * params: sid, token, newnick
     */
    public static final String modifyNick = baseUrl + "passport/modify";


    /*****************************************   Course   ****************************************/
    /**
     * 获取课程列表
     * get
     */
    public static final String getCourses = baseUrl + "course/getcourses";

    /**
     * 加入课程
     * post
     * params: token, sid, id-课程id
     */
    public static final String joinClass = baseUrl + "course/joinclass";

    /**
     * 搜索课程
     * post
     * params: token, sid, id-课程id
     */
    public static final String searchClass = baseUrl + "course/searchclass";

    /**
     * 退出课程
     * post
     * params: token, sid, id-课程id
     */
    public static final String dropClass = baseUrl + "course/dropclass";

    /**
     * 获取我加入的课程
     * get
     * params: token, sid
     */
    public static final String getMyCourses = baseUrl + "course/getmycourses";

    /**
     * 获取课程相关资料
     * get
     * params: token, sid, id(courseid)
     */
    public static final String getCourseDocs = baseUrl + "course/getcoursedocs";

    /**
     * 课程详情
     * post
     */
    public static final String aboutCourse = baseUrl + "course/aboutcourse";

    /**
     * 创建课程
     * post
     * params: token, id, sid, name, about, teacher, other
     */
    public static final String createCourse = baseUrl + "course/createcourse";

    /**
     * 删除课程
     * post
     * params: id, token, sid
     */
    public static final String deleteCourse = baseUrl + "course/deletecourse";

    /**
     * 恢复课程
     * post
     * params: token, sid, id
     */
    public static final String recoverCourse = baseUrl + "course/recoverycourse";

    /**
     * 修改课程
     * post
     * params: token, id, sid, name, about, teacher, other
     */
    public static final String modifyCourse = baseUrl + "course/modifycourse";

    /**
     * 获取某个资料
     * get
     * params: token, sid, id
     */
    public static final String getCourseDoc = baseUrl + "course/getcoursedoc";

    /**
     * 添加课程资料
     * post
     * params: token, sid, id-课程id, name-资料名称
     */
    public static final String addCourseDoc = baseUrl + "course/addcoursedoc";

    /**
     * 删除课程资料
     * post
     * params: token, sid, id-资料id
     */
    public static final String deleteCourseDoc = baseUrl + "course/deletecoursedoc";

    /*****************************************   Group   ****************************************/
    /**
     * 创建小组
     * post
     * params: token, sid, name, about
     */
    public static final String createGroup = baseUrl + "group/creategroup";

    /**
     * 获取小组列表
     * get
     * params: cid
     */
    public static final String getGroups = baseUrl + "group/getgroups";

    /**
     * 获取我加入的小组
     * get
     * params: token, sid, cid
     */
    public static final String getMyGroup = baseUrl + "group/getmygroup";

    /**
     * e
     * 获取小组信息
     * get
     * params: token, sid, gid
     */
    public static final String getGroupInfo = baseUrl + "group/getgroupinfo";

    /**
     * 删除小组
     * post
     * params: gid, token, sid
     */
    public static final String deleteGroup = baseUrl + "group/deletegroup";

    /**
     * 恢复删除小组
     * post
     * params: gid, token , sid
     */
    public static final String recoverGroup = baseUrl + "group/recoverygroup";

    /**
     * 加入小组
     * post
     * params: gid, token, sid
     */
    public static final String joinGroup = baseUrl + "group/joingroup";

    /**
     * 退出小组
     * post
     * params:gid, token, sid
     */
    public static final String quitGroup = baseUrl + "group/quitgroup";

    /**
     * 获取小组资料列表
     * get
     * params:token, sid, gid
     */
    public static final String getGroupDocs = baseUrl + "group/getgroupdocs";

    /**
     * 获取某个资料
     * get
     * params:token, sid, did
     */
    public static final String getGroupDoc = baseUrl + "group/getgroupdoc";

    /**
     * 添加小组资料
     * post
     * params: token, sid, gid, name
     */
    public static final String addGroupDoc = baseUrl + "group/addgroupdoc";

    /**
     * 删除小组资料
     * post
     * params:token, sid, did
     */
    public static final String deleteGroupDoc = baseUrl + "group/deletegroupdoc";

    /**
     * 获取我的资料列表
     * get
     * params:token, sid
     */
    public static final String getMyDocs = baseUrl + "group/getmydocs";


    /*****************************************   Homework   ****************************************/
    /**
     * 获取作业列表
     * get
     * params:token, sid, cid
     */
    public static final String getJobs = baseUrl + "work/getjobs";

    /**
     * 获取作业的附件
     * get
     * params:token, sid, jid
     */
    public static final String getJobFiles = baseUrl + "work/getjobfiles";

    /**
     * 删除个人作业
     * post
     * params:sid, token, jid
     */
    public static final String deleteJob = baseUrl + "work/deletejob";

    /**
     * 我的作业列表
     * get
     * params:token, sid, cid
     */
    public static final String getMyJobs = baseUrl + "work/getmyjobs";

    /**
     * 提交作业
     * post
     * params:token, sid, jid-作业id, jtype(作业类型: 1-小组作业，2-个人作业)
     */
    public static final String submitJob = baseUrl + "work/submitjob";

    /**
     * 我提交和我的小组提交的作业
     * get
     * params:token, sid
     */
    public static final String getMySubmitJobs = baseUrl + "work/getmysubmitjobs";

    /*****************************************   Cloud   ****************************************/
    /**
     * 获取云盘文件列表
     * get
     * params:token, sid
     */
    public static final String getCloudObjects = baseUrl + "cloud/getcloudobjects";

    /**
     * 向云盘传文件
     * post
     * params:token, sid
     */
    public static final String uploadObject = baseUrl + "cloud/uploadobject";

    /**
     * 删除文件
     * post
     * params:token, sid, uuid
     */
    public static final String deleteObject = baseUrl + "cloud/deleteobject";


    /*****************************************   File Upload/Download   ****************************************/
    /**
     * Upload
     * post
     * params:uuid, file
     */
    public static final String upload = uploadBaseUrl + "file/upload";

    /**
     * Download
     * get
     * params:uuid, filename, sid
     * param:cid (云盘不传)
     * param:gid (云盘不传), 课件资料gid="doc", 老师发布的作业资料gid="doc", 个人提交的作业gid="job", 小组资料gid=groupId, 小组作业gid=groupId
     */
    public static final String download = uploadBaseUrl + "file/download";

    /**
     * 获取附件列表
     * get
     * params:uuid, sid, token
     */
    public static final String getAttachs = baseUrl + "files/getattachs";

    /**
     * 更新作业提交文件
     * get
     * params:token, sid, uuid
     */
    public static final String updateFile = baseUrl + "files/updatefile";

    /*****************************************   Notify   ****************************************/
    /**
     * 获取我最近的通知
     * get
     * params:sid, token
     */
    public static final String getRecent = baseUrl + "notify/getrecent";

    /**
     * 某条通知已读
     * get
     * params:sid, token, notifyid
     */
    public static final String hasRead = baseUrl + "notify/hasread";

    /**
     * 通知全部已读
     * get
     * params:sid, token
     */
    public static final String hasReadAll = baseUrl + "notify/hasreadall";

    /**
     * 获取历史消息
     * get
     * params:sid, token
     */
    public static final String getHistoryMsg = baseUrl + "notify/gethistory";


    /**
     * 反馈信息
     * post
     * params:sid, token, message
     */
    public static final String addFeedBack = baseUrl + "feedback/add";

    /**
     * 回传异常解密事件
     * post
     * params:sid, info
     */
    public static final String uploadEvent = baseUrl + "admin/addEvent";
}
