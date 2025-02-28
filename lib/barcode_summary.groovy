import java.nio.file.Path

class BarcodeSummary {
    Path summary_file

    BarcodeSummary(Path summary_file) {
        this.summary_file = summary_file
    }

    // The summary_file is a CSV file with the following columns:
    // barcode, total, duplicate, unmapped, lowmapq
    // Extract the following statistics:
    // Overall alignment (mapping) rate: (total - unmapped)/total
    // Total unique fragments: total - duplicate - unmapped - lowmapq
    // Duplication rate (saturation): duplicate/(total - unmapped - lowmapq)

    def getStatistics() {
        def total = 0
        def duplicate = 0
        def unmapped = 0
        def lowmapq = 0

        summary_file.eachLine { line, index ->
            if (index == 0) return // Skip header line
            def data = line.split(',')
            total += data[1] as int
            duplicate += data[2] as int
            unmapped += data[3] as int
            lowmapq += data[4] as int
        }

        def unique = total - duplicate - unmapped - lowmapq
        def alignment_rate = (total - unmapped) / total
        def duplication_rate = duplicate / (total - unmapped - lowmapq)
        return [alignment_rate: alignment_rate, unique_fragments: unique, duplication_rate: duplication_rate]
    }

    // Override the toString method to return a JSON file with the statistics
    String toString() {
        def stats = getStatistics()
        return new groovy.json.JsonBuilder(stats).toPrettyString()
    }
}