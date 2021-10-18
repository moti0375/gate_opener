import 'package:gate_opener/data/model/gate.dart';

abstract class GateOpenerRepository{
  Future<dynamic> createGate(Gate gate);
  Future<dynamic> deleteGate(Gate gate);
  Stream<List<Gate>> fetchAllGates();
}