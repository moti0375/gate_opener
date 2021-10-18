import 'package:gate_opener/channel/channel_adapter.dart';
import 'package:gate_opener/data/datasources/gate_opener_datasource.dart';
import 'package:gate_opener/data/model/gate.dart';

class GateOpenerPlatformDataSource implements GateOpenerDataSource {

  ChannelAdapter _adapter;

  GateOpenerPlatformDataSource(this._adapter);

  @override
  Future<void> createGate(Gate gate) {
    return _adapter.invokeMethod("createGate", gate.toJson());
  }

  @override
  Future<void> deleteGate(Gate gate) {
    return _adapter.invokeMethod("deleteGate", gate.toJson());
  }

  @override
  Stream<dynamic> getAllGates() {
    return _adapter.getEventChannel();
  }
}