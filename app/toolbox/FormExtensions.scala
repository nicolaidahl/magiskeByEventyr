package toolbox

import play.api.data.format.Formatter
import play.api.data.FormError

object FormExtensions {
/**
   * Default formatter for the `String` type.
   */
  implicit def stringFormat: Formatter[String] = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key).toRight(Seq(FormError(key, "error.required", Nil)))
    def unbind(key: String, value: String) = Map(key -> value)
  }
  
  /**
     * Default formatter for the `Double` type.
     */
    implicit def doubleFormat: Formatter[Double] = new Formatter[Double] {

      override val format = Some("format.real", Nil)

      def bind(key: String, data: Map[String, String]) =
        parsing(_.toDouble, "error.real", Nil)(key, data)

      def unbind(key: String, value: Double) = Map(key -> value.toString)
    }

    /**
     * Helper for formatters binders
     * @param parse Function parsing a String value into a T value, throwing an exception in case of failure
     * @param error Error to set in case of parsing failure
     * @param key Key name of the field to parse
     * @param data Field data
     */
    private def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      stringFormat.bind(key, data).right.flatMap { s =>
        util.control.Exception.allCatch[T]
          .either(parse(s))
          .left.map(e => Seq(FormError(key, errMsg, errArgs)))
      }
    }
}