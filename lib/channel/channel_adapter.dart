const String BASE_PLATFORM_CHANNEL = "com.bartovapps.gate_opener.channel";
const String EVENTS_ENDPOINT = ".events";
abstract class ChannelAdapter{
  Future<dynamic> invokeMethod(String method, Map<String, dynamic> params);
  Stream<dynamic> getEventChannel();
}