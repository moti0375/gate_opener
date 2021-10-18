import 'package:flutter/services.dart';
import 'package:gate_opener/channel/channel_adapter.dart';

class ChannelAdapterImpl extends ChannelAdapter {

  final String channelName;
  late EventChannel _eventChannel;
  late MethodChannel _methodsChannel;

  ChannelAdapterImpl({this.channelName = BASE_PLATFORM_CHANNEL}) {
    _eventChannel = EventChannel("$channelName$EVENTS_ENDPOINT");
    _methodsChannel = MethodChannel(channelName);
  }

  @override
  Stream getEventChannel() {
    return _eventChannel.receiveBroadcastStream();
  }

  @override
  Future invokeMethod(String method, Map<String, dynamic> params) {
    return _methodsChannel.invokeMapMethod(method, params);
  }
}
