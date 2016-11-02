window.onload = function (e) {
  var tables = document.getElementsByTagName('table');

  for (var i = 0, table; table = tables[i]; i++) {
    setupTableForSorting(table)
  }
};

setupTableForSorting = function(table) {
  affixLastRow(table);
  addSortByForGroups(table);

  new Tablesort(table, {
    descending: true
  });
};

affixLastRow = function(table) {
  var lastRow = table.rows[ table.rows.length - 1 ];

  var lastRowIsTotalRow = lastRow.cells[0].innerHTML.includes("Total");

  if (lastRowIsTotalRow) {
    lastRow.classList += " no-sort";
  }
};

addSortByForGroups = function(table) {
  var headerRow = table.rows[0];

  var extractGroupPattern = /([A-Z]+) \(.*\)/g;

  if (headerRow.cells[0].innerHTML === "Group") {
    for (var i = 0, row; row = table.rows[i]; i++) {
      for (var j = 0, cell; cell = row.cells[j]; j++) {

        var match = extractGroupPattern.exec(cell.innerHTML);

        if (match != null) {
          var groupCode = match[1];

          if (groupCode.length == 1) {
            groupCode = " " + groupCode
          }

          cell.setAttribute("data-sort", groupCode);
        }
      }
    }
  }
};
