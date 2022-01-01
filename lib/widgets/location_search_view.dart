import 'package:flutter/material.dart';

class LocationSearchView extends StatelessWidget {
  const LocationSearchView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          color: Colors.black.withOpacity(0.6),
          padding: const EdgeInsets.all(8.0),
          child: TextField(
            decoration: InputDecoration(
                prefixIcon: Icon(
              Icons.search,
              color: Colors.white,
            )),
          ),
        ),
        Expanded(
          child: Visibility(
              visible: false,
              child: Container(
                decoration: BoxDecoration(color: Colors.black.withOpacity(0.6), backgroundBlendMode: BlendMode.darken),
                child: ListView.builder(
                    shrinkWrap: true,
                    itemCount: 10,
                    itemBuilder: (context, index) => ListTile(
                          title: Text("Address 1, TelAviv", style: Theme.of(context).textTheme.caption,),
                        )),
              )),
        )
      ],
    );
  }
}
