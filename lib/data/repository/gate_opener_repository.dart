import 'package:gate_opener/data/model/gate.dart';

abstract class GateOpenerRepository{
  Future<dynamic> createGate(Gate gate);
  Future<dynamic> deleteGate(double id);
  Stream<List<Gate>> fetchAllGates();
}