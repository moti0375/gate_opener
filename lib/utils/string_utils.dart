abstract class StringUtils {
  static const EMPTY_STRING = "";
  static const DASH = "-";

  static get multiplySymbol => String.fromCharCode(0x0D7);

  static bool isEmpty(String string) {
    return string.isEmpty;
  }
}