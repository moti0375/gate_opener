import 'package:gate_opener/data/model/gate.dart';

abstract class GateOpenerDataSource{
  Future<void> createGate(Gate gate);
  Stream<dynamic> getAllGates();
  Future<void> deleteGate(Gate gate);
}