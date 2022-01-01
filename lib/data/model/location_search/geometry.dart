import 'package:gate_opener/data/model/location.dart';

class Geometry{
  final Location location;
  Geometry({required this.location});

  factory Geometry.fromJson(Map<dynamic, dynamic> json){
    var location = Location.fromSearchJson(json['location']);
    return Geometry(location: location);
  }
}