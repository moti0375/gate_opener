import 'package:gate_opener/data/model/gate.dart';

abstract class HomePageEvent{}
class GetAllGates extends HomePageEvent{}
class GatesLoadedEvent extends HomePageEvent{
  final List<Gate> gates;
  GatesLoadedEvent(this.gates);
}

class DeleteGate extends HomePageEvent{
  final double id;
  DeleteGate(this.id);
}

class AddGate extends HomePageEvent{
  AddGate();
}