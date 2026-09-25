import React, { useState } from 'react';
import {
  Sparkles,
  CheckCircle2,
  AlertCircle,
  MapPin,
  Calendar,
  ArrowRight,
  ShieldCheck,
  Check,
  ChevronRight,
  HelpCircle,
  Layers,
  Eye,
  Sliders,
  Clock
} from 'lucide-react';
import { MatchCandidate } from '../types';

interface MatchCenterViewProps {
  matches: MatchCandidate[];
  onClaimItem: (foundReportId: number, lostReportId?: number) => void;
  onNavigate: (view: string) => void;
}

export const MatchCenterView: React.FC<MatchCenterViewProps> = ({
  matches,
  onClaimItem,
  onNavigate
}) => {
  const [selectedMatchIndex, setSelectedMatchIndex] = useState<number>(0);
  const activeMatch = matches[selectedMatchIndex] || matches[0];

  if (matches.length === 0) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <div className="w-16 h-16 bg-sky-50 text-sky-600 rounded-3xl flex items-center justify-center mx-auto mb-4">
          <Sparkles className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-black text-slate-900">No Potential Matches Discovered Yet</h2>
        <p className="text-sm text-slate-600 mt-2 max-w-md mx-auto font-medium">
          Our AI matching engine scans all campus found reports against your lost items continuously. When a report matches, you will receive an alert.
        </p>
        <div className="mt-8 flex justify-center gap-4">
          <button
            onClick={() => onNavigate('report-lost')}
            className="px-6 py-3 rounded-2xl bg-sky-600 hover:bg-sky-700 text-white font-bold text-xs shadow-md transition-colors"
          >
            File a Lost Item Report
          </button>
          <button
            onClick={() => onNavigate('browse')}
            className="px-6 py-3 rounded-2xl bg-white border border-slate-300 text-slate-700 font-bold text-xs hover:bg-slate-50 transition-colors"
          >
            Browse All Found Items
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Header */}
      <div className="text-center max-w-2xl mx-auto mb-10">
        <div className="inline-flex items-center space-x-2 bg-indigo-50 text-indigo-700 px-3.5 py-1.5 rounded-full text-xs font-bold mb-3 border border-indigo-200">
          <Sparkles className="w-4 h-4 text-indigo-600" />
          <span>Explainable Multi-Signal AI Matching</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
          Smart Match Center
        </h1>
        <p className="text-sm text-slate-600 mt-1 font-medium">
          Compare candidate found items against your lost reports. Inspect individual signals and verify ownership.
        </p>
      </div>

      {/* Match Selector Tabs if multiple */}
      {matches.length > 1 && (
        <div className="flex items-center space-x-3 overflow-x-auto pb-4 mb-6">
          {matches.map((m, idx) => (
            <button
              key={m.id}
              onClick={() => setSelectedMatchIndex(idx)}
              className={`p-3.5 rounded-2xl border text-left transition-all min-w-[260px] flex-shrink-0 ${
                selectedMatchIndex === idx
                  ? 'border-indigo-500 bg-indigo-50/70 shadow-sm ring-2 ring-indigo-200'
                  : 'border-slate-200 bg-white hover:border-slate-300'
              }`}
            >
              <div className="flex items-center justify-between mb-1.5">
                <span className="text-[10px] font-mono text-slate-500 font-bold">
                  {m.lostReferenceId} ↔ {m.foundReferenceId}
                </span>
                <span className={`text-xs font-black px-2.5 py-0.5 rounded-full ${
                  m.overallScore >= 80 ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'
                }`}>
                  {m.overallScore}% Match
                </span>
              </div>
              <p className="text-xs font-extrabold text-slate-900 truncate">{m.lostTitle}</p>
            </button>
          ))}
        </div>
      )}

      {/* Main Comparison Container */}
      <div className="bg-white rounded-3xl border border-slate-200/90 shadow-sm overflow-hidden mb-8">
        {/* Top Match Score Bar */}
        <div className="p-6 bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white flex flex-col sm:flex-row items-center justify-between gap-5">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-emerald-400 to-teal-500 text-slate-950 flex flex-col items-center justify-center font-black shadow-lg shadow-emerald-500/20 flex-shrink-0">
              <span className="text-2xl leading-none">{activeMatch.overallScore}%</span>
              <span className="text-[9px] uppercase tracking-wider font-extrabold mt-0.5">Match</span>
            </div>
            <div>
              <div className="inline-flex items-center space-x-1.5 text-xs text-emerald-400 font-bold">
                <CheckCircle2 className="w-3.5 h-3.5" />
                <span>Potential Match ({activeMatch.overallScore}%)</span>
              </div>
              <h2 className="text-xl font-black text-white mt-0.5">
                High Correlation Detected
              </h2>
              <p className="text-xs text-slate-300 font-medium">
                Transparent multi-signal breakdown across semantic, spatial, temporal, and visual indicators.
              </p>
            </div>
          </div>

          <button
            onClick={() => onClaimItem(activeMatch.foundReportId, activeMatch.lostReportId)}
            className="w-full sm:w-auto px-7 py-3.5 rounded-2xl bg-gradient-to-r from-emerald-400 to-teal-400 hover:from-emerald-300 hover:to-teal-300 text-slate-950 font-black text-xs uppercase tracking-wider shadow-lg shadow-emerald-500/25 transition-all flex items-center justify-center space-x-2"
          >
            <span>Claim This Item</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

        {/* Side-by-Side Comparison: YOUR LOST ITEM vs POSSIBLE FOUND ITEM */}
        <div className="p-6 sm:p-8 grid grid-cols-1 lg:grid-cols-2 gap-8 border-b border-slate-100">
          {/* LEFT: Your Lost Report */}
          <div className="p-6 rounded-3xl bg-amber-50/50 border border-amber-200">
            <div className="flex items-center justify-between mb-4">
              <span className="px-3.5 py-1 rounded-full text-xs font-black bg-amber-100 text-amber-900 uppercase tracking-wider">
                YOUR LOST ITEM
              </span>
              <span className="text-xs font-mono font-bold text-amber-800">
                {activeMatch.lostReferenceId}
              </span>
            </div>

            {activeMatch.lostImageUrl ? (
              <img
                src={activeMatch.lostImageUrl}
                alt="Lost Item Reference"
                className="w-full h-52 object-cover rounded-2xl mb-4 border border-amber-200 shadow-sm"
              />
            ) : (
              <div className="w-full h-40 bg-amber-100/50 rounded-2xl mb-4 flex items-center justify-center text-amber-600 font-semibold text-xs">
                No reference photo provided
              </div>
            )}

            <h3 className="font-black text-slate-900 text-lg mb-1">{activeMatch.lostTitle}</h3>

            <div className="mt-4 space-y-2.5 text-xs">
              <div className="flex items-center justify-between py-1.5 border-b border-amber-100">
                <span className="text-slate-500 font-semibold">Category</span>
                <span className="font-extrabold text-slate-800">{activeMatch.lostCategory}</span>
              </div>
              <div className="flex items-center justify-between py-1.5 border-b border-amber-100">
                <span className="text-slate-500 font-semibold">Last Seen Location</span>
                <span className="font-extrabold text-slate-800 flex items-center space-x-1">
                  <MapPin className="w-3.5 h-3.5 text-amber-600" />
                  <span>{activeMatch.lostLocation}</span>
                </span>
              </div>
              <div className="flex items-center justify-between py-1.5 border-b border-amber-100">
                <span className="text-slate-500 font-semibold">Reported Lost Date</span>
                <span className="font-extrabold text-slate-800 flex items-center space-x-1">
                  <Calendar className="w-3.5 h-3.5 text-slate-400" />
                  <span>{new Date(activeMatch.lostDate).toLocaleDateString()}</span>
                </span>
              </div>
            </div>
          </div>

          {/* RIGHT: Candidate Found Report */}
          <div className="p-6 rounded-3xl bg-emerald-50/50 border border-emerald-200">
            <div className="flex items-center justify-between mb-4">
              <span className="px-3.5 py-1 rounded-full text-xs font-black bg-emerald-100 text-emerald-900 uppercase tracking-wider">
                POSSIBLE FOUND ITEM
              </span>
              <span className="text-xs font-mono font-bold text-emerald-800">
                {activeMatch.foundReferenceId}
              </span>
            </div>

            {activeMatch.foundImageUrl ? (
              <img
                src={activeMatch.foundImageUrl}
                alt="Found Item Photo"
                className="w-full h-52 object-cover rounded-2xl mb-4 border border-emerald-200 shadow-sm"
              />
            ) : (
              <div className="w-full h-40 bg-emerald-100/50 rounded-2xl mb-4 flex items-center justify-center text-emerald-600 font-semibold text-xs">
                Found photo stored
              </div>
            )}

            <h3 className="font-black text-slate-900 text-lg mb-1">{activeMatch.foundTitle}</h3>

            <div className="mt-4 space-y-2.5 text-xs">
              <div className="flex items-center justify-between py-1.5 border-b border-emerald-100">
                <span className="text-slate-500 font-semibold">Category</span>
                <span className="font-extrabold text-slate-800">{activeMatch.foundCategory}</span>
              </div>
              <div className="flex items-center justify-between py-1.5 border-b border-emerald-100">
                <span className="text-slate-500 font-semibold">Found Location</span>
                <span className="font-extrabold text-slate-800 flex items-center space-x-1">
                  <MapPin className="w-3.5 h-3.5 text-emerald-600" />
                  <span>{activeMatch.foundLocation}</span>
                </span>
              </div>
              <div className="flex items-center justify-between py-1.5 border-b border-emerald-100">
                <span className="text-slate-500 font-semibold">Found Date</span>
                <span className="font-extrabold text-slate-800 flex items-center space-x-1">
                  <Calendar className="w-3.5 h-3.5 text-slate-400" />
                  <span>{new Date(activeMatch.foundDate).toLocaleDateString()}</span>
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Multi-Signal Breakdown Metrics */}
        <div className="p-6 sm:p-8 bg-[#f8fafc]">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-black text-slate-900 flex items-center space-x-2">
              <Sliders className="w-4 h-4 text-sky-600" />
              <span>Multi-Signal Contribution Breakdown</span>
            </h3>
            <span className="text-xs text-slate-500 font-medium">Explainable match factors</span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
            {/* Category Score */}
            <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm">
              <div className="flex items-center justify-between text-xs mb-1">
                <span className="font-bold text-slate-600">Category</span>
                <span className="font-black text-slate-900">{activeMatch.categoryScore || 100}%</span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
                <div className="bg-sky-500 h-full rounded-full" style={{ width: `${activeMatch.categoryScore || 100}%` }} />
              </div>
            </div>

            {/* Location Score */}
            <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm">
              <div className="flex items-center justify-between text-xs mb-1">
                <span className="font-bold text-slate-600">Location Proximity</span>
                <span className="font-black text-slate-900">{activeMatch.locationScore || 75}%</span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
                <div className="bg-indigo-500 h-full rounded-full" style={{ width: `${activeMatch.locationScore || 75}%` }} />
              </div>
            </div>

            {/* Semantic Similarity */}
            <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm">
              <div className="flex items-center justify-between text-xs mb-1">
                <span className="font-bold text-slate-600">Semantic Similarity</span>
                <span className="font-black text-slate-900">{activeMatch.semanticScore || 80}%</span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
                <div className="bg-emerald-500 h-full rounded-full" style={{ width: `${activeMatch.semanticScore || 80}%` }} />
              </div>
            </div>

            {/* Time Compatibility */}
            <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm">
              <div className="flex items-center justify-between text-xs mb-1">
                <span className="font-bold text-slate-600">Time Compatibility</span>
                <span className="font-black text-slate-900">{activeMatch.timeScore || 85}%</span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
                <div className="bg-amber-500 h-full rounded-full" style={{ width: `${activeMatch.timeScore || 85}%` }} />
              </div>
            </div>
          </div>

          {/* Why This May Match Bullet Points */}
          <div className="mb-4">
            <h4 className="text-xs font-black text-slate-800 uppercase tracking-wider mb-2 flex items-center space-x-1.5">
              <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
              <span>Why This May Match:</span>
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2.5">
              {activeMatch.matchReasons.map((reason, i) => (
                <div
                  key={i}
                  className="p-3 bg-white rounded-xl border border-slate-200 shadow-sm flex items-center space-x-2 text-xs font-bold text-slate-800"
                >
                  <div className="w-5 h-5 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center flex-shrink-0 font-black text-[10px]">
                    ✓
                  </div>
                  <span>{reason.replace(/^[✓~]\s*/, '')}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Action CTA */}
          <div className="mt-8 pt-6 border-t border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="text-xs text-slate-500 flex items-center space-x-1.5 font-medium">
              <HelpCircle className="w-4 h-4 text-slate-400" />
              <span>Is this your lost item? Submit answers to private verification questions to claim it.</span>
            </div>
            <button
              onClick={() => onClaimItem(activeMatch.foundReportId, activeMatch.lostReportId)}
              className="px-8 py-3.5 rounded-2xl bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 text-white font-black text-xs uppercase tracking-wider shadow-lg shadow-emerald-500/25 transition-all flex items-center space-x-2"
            >
              <span>Verify & Claim Item</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
