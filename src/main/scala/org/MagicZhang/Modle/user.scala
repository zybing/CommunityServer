package org.MagicZhang.Modle

/**
  * Created by sonof on 2017/2/21.
  * 用于将数据库的user表数据保存到该类中，同时提供内存中备份
  */
class user(val phone_number:String,var user_name:String,val user_type:Byte,var last_updatetime:String
          ="0000-00-00 00:00:01",var last_updatelocation:String="0,0",
           var help_number:Int=0,var request_number:Int=0
          ,var isonline:Byte=0,var status_requester:Byte,var status_helper:Byte
          ,var current_taskid:String,var online_time:Int) {

  //更新task中helpnumber的数值，offset是要增加的数值
  // ，如果offset是1就是帮助次数增加1
  def update_helpnumber(offset:Int) {help_number+=offset}
  //更新task中requestnumber的数值，offset是要增加的数值
  // ，如果offset是1就是帮助次数增加1
  def update_requestnumber(offset:Int) {request_number+=offset}
}
