package org.MagicZhang.Modle

/**
  * Created by sonof on 2017/2/21.
  */
class task (_task_id:String,_request_phone_number:String,_request_time:String
           ,_request_location:String,_request_info:String
            ,_volunteer_phone_number:String,_ack_time:String,_ack_location:String){
  val task_id=_task_id
  val request_phone_number=_request_phone_number
  val request_time=_request_time
  val request_location=_request_location
  val request_info=_request_info
  val volunteer_phone_number=_volunteer_phone_number
  var ack_time=_ack_time
  var ack_location=_ack_location
}
