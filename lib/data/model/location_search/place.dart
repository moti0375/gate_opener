import 'package:gate_opener/data/model/location_search/geometry.dart';
import 'package:equatable/equatable.dart';


class Place extends Equatable{
  final Geometry geometry;
  final String name;
  final String formattedAddress;

  Place({required this.geometry, required this.name, required this.formattedAddress});

  factory Place.fromJson(Map<String, dynamic> json){
    String name = json['name'];
    String formattedAddress = json['formatted_address'];
    Geometry geometry = Geometry.fromJson(json['geometry']);
    return Place(name: name, formattedAddress: formattedAddress, geometry: geometry);
  }

  @override
  List<Object?> get props => [geometry, name, formattedAddress];
  @override
  bool? get stringify => true;
}