package org.MagicZhang.Modle

/**
  * Created by sonof on 2017/2/21.
  */
class task (val task_id:String,val request_phone_number:String,val request_time:String
           ,val request_location:String,val request_info:String
            ,var volunteer_phone_number:String,var ack_time:String,var ack_location:String
           ,var status:Byte,var ordertime:Long,var fileurl:String,var file_status:Byte){
}
