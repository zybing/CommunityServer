package org.MagicZhang.Modle

/**
  * Created by sonof on 2017/2/21.
  */
class user(val phone_number:String,var user_name:String,val user_type:Byte,var last_updatetime:String
          ="0000-00-00 00:00:01",var last_updatelocation:String="0,0",
           var help_number:Int=0,var request_number:Int=0
          ,var isonline:Byte=0,var status_requester:Byte,var status_helper:Byte
          ,var current_taskid:String,var online_time:Int) {

  def update_helpnumber(offset:Int) {help_number+=offset}
  def update_requestnumber(offset:Int) {request_number+=offset}
}
