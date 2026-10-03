# .\run-tests.ps1

mvn -q compile

foreach ($name in "stage1", "stage2") {
    Remove-Item "tests\$name.csv" -ErrorAction SilentlyContinue
    Get-Content "tests\$name.txt" | java -cp target\classes Main "tests\$name.csv" > "tests\$name-out.txt"
}

Remove-Item tests\stage3.csv -ErrorAction SilentlyContinue
Get-Content tests\stage3-run1.txt | java -cp target\classes Main tests\stage3.csv > tests\stage3-run1-out.txt
Get-Content tests\stage3-run2.txt | java -cp target\classes Main tests\stage3.csv > tests\stage3-run2-out.txt

Write-Host "Done. Check tests\*-out.txt"