window.onload = function (e) {
  var tables = document.getElementsByTagName('table');

  for (var i = 0, table; table = tables[i]; i++) {
    setupTableForSorting(table)
  }
};

setupTableForSorting = function(table) {
  // setSortByValues(table);
  affixLastRow(table);

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
