import 'package:flutter/material.dart';
import 'package:gate_opener/widgets/location_search_view/search_view_notifier.dart';
import 'package:provider/provider.dart';

class LocationSearchView extends StatelessWidget {
  const LocationSearchView({Key? key}) : super(key: key);

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
                        searchViewNotifier.onSearchItemSelected();
                      },
                          title: Text(searchViewNotifier.searchResults[index].description, style: Theme.of(context).textTheme.caption,),
                        )),
              )),
        )
      ],
    );
  }
}
