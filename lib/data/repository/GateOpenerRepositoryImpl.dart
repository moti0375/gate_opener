import 'dart:convert';

import 'package:gate_opener/data/datasources/gate_opener_datasource.dart';
import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';

class GateOpenerRepositoryImpl implements GateOpenerRepository{

  GateOpenerDataSource _dataSource;
  GateOpenerRepositoryImpl(this._dataSource);

  @override
  Future createGate(Gate gate) {
    return _dataSource.createGate(gate);
  }

  @override
  Future deleteGate(double id) {
   return _dataSource.deleteGate(id);
  }

  @override
  Stream<List<Gate>> fetchAllGates() {
    return _dataSource.getAllGates().map((event) => _mapResultToList(event.cast<Map<Object?, Object?>>()));
  }

  List<Gate> _mapResultToList(List<Map<Object?, Object?>> data){
    return data.map((e) => Gate.fromJson(jsonDecode(jsonEncode(e)))).toList();
  }
}