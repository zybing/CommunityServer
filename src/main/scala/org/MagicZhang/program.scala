package org.MagicZhang
import org.MagicZhang.Sql.Util.jdbcUtils
/**
  * Created by sonof on 2017/2/21.
  */
object program {
  def main(args:Array[String]): Unit ={
    val connection=jdbcUtils.getConnection()

  }
}
