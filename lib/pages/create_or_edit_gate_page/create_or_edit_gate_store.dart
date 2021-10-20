import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/model/location.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:mobx/mobx.dart';

part 'create_or_edit_gate_store.g.dart';

class CreateOrEditStore extends CreateOrEditGateBase with _$CreateOrEditStore{
  CreateOrEditStore(GateOpenerRepository repository) : super(repository);
}

abstract class CreateOrEditGateBase with Store {

  GateOpenerRepository _repository;

  CreateOrEditGateBase(this._repository);

  @observable
  LatLng? location;

  @observable
  String? phoneNumber;

  @observable
  String? name;

  @computed
  bool get formValid => _checkFormValidity(location, phoneNumber, name);

  @observable
  bool saveSuccess = false;

  @action
  void setName(String name){
    print("setName: $name");
    this.name = name;
  }

  @action
  void setLocationChanged(LatLng position){
    print("setLocationChanged: $position");
    this.location = position;
  }

  @action
  void setPhoneNumber(String phoneNumber){
    print("setPhoneNumber: $phoneNumber");
    this.phoneNumber = phoneNumber;
  }

  void submit(){
    if(location != null){
      Gate gate =  Gate( location: Location(location!.latitude, location!.longitude), name: name!, phoneNumber: phoneNumber!);
      _repository.createGate(gate).then((value) => saveSuccess = true);
    }
  }

  bool _checkFormValidity(LatLng? location, String? phoneNumber, String? name) {
    print("_checkFormValidity: ");
    return _checkNameValidity(name) && _checkPhoneValidity(phoneNumber) && _checkLocationValidity(location);
  }

  bool _checkNameValidity(String? name) {
    return (name != null && name.length > 2);
  }

  bool _checkPhoneValidity(String? phoneNumber){
    return (phoneNumber != null && phoneNumber.isNotEmpty);
  }

  bool _checkLocationValidity(LatLng? location){
    return location != null;
  }


}