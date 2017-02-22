package org.MagicZhang.Modle

/**
  * Created by sonof on 2017/2/21.
  */
class user(_phone_number:String,_usertype:Byte,_last_updatetime:String
          ,_last_updatelocation:String,_help_number:Int,_request_number:Int
          ,_isonline:Byte) {
  val phone_number=_phone_number
  val user_type=_usertype
  var last_updatetime=_last_updatetime
  var last_updatelocation=_last_updatelocation
  var help_number=_help_number
  var request_number=_request_number
  var isonline=_isonline
  def update_helpnumber(offset:Int) {help_number+=offset}
  def update_requestnumber(offset:Int) {request_number+=offset}
}
