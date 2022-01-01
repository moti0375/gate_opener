import 'dart:async';

import 'package:flutter/material.dart';
import 'package:gate_opener/data/model/location_search/place.dart';
import 'package:gate_opener/widgets/location_search_view/search_view_notifier.dart';
import 'package:provider/provider.dart';

class LocationSearchView extends StatefulWidget {
  final ValueChanged<Place> onPlaceSelected;
  const LocationSearchView({Key? key, required this.onPlaceSelected}) : super(key: key);


  @override
  _LocationSearchViewState createState() => _LocationSearchViewState();
}

class _LocationSearchViewState extends State<LocationSearchView> {

  StreamSubscription? _subscription;
  @override
  void initState() {
    super.initState();
    if(_subscription == null){
      var notifierStream = Provider.of<SearchViewNotifier>(context, listen: false).streamController.stream;
      _subscription ??= notifierStream.listen((place) {
        print("notifierStream: $place");
        widget.onPlaceSelected(place);
      });
    }
  }


  @override
  void dispose() {
    _subscription?.cancel();
    _subscription = null;
    print("searchView: dispose");
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {

    final searchViewNotifier = Provider.of<SearchViewNotifier>(context);
    print("searchViewNotifier: build:");

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          color: Colors.black.withOpacity(0.6),
          padding: const EdgeInsets.all(8.0),
          child: TextField(
            style: Theme.of(context).textTheme.bodyText2?.copyWith(color: Colors.white),
            onChanged: (text) {
              searchViewNotifier.searchPlace(text);
            },
            decoration: InputDecoration(
                prefixIcon: Icon(
              Icons.search,
              color: Colors.white,
            )),
          ),
        ),
        Expanded(
          child: Visibility(
              visible: searchViewNotifier.searchResults.isNotEmpty,
              child: Container(
                decoration: BoxDecoration(color: Colors.black.withOpacity(0.6), backgroundBlendMode: BlendMode.darken),
                child: ListView.builder(
                    shrinkWrap: true,
                    itemCount: searchViewNotifier.searchResults.length,
                    itemBuilder: (context, index) => ListTile(
                      onTap: () {
                        searchViewNotifier.onSearchItemSelected(searchViewNotifier.searchResults[index].placeId);
                      },
                          title: Text(searchViewNotifier.searchResults[index].description, style: Theme.of(context).textTheme.caption,),
                        )),
              )),
        )
      ],
    );
  }
}
