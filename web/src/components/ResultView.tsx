import React, { useState } from 'react';
import { Printer, Bookmark, BookmarkCheck, Share2, ArrowLeft, Check, ShieldCheck, MapPin } from 'lucide-react';
import { LandRecordResult } from '../types';

interface ResultViewProps {
  result: LandRecordResult;
  onBack: () => void;
  onSave: (result: LandRecordResult) => void;
  isSaved: boolean;
  lang: 'en' | 'bn';
}

export const ResultView: React.FC<ResultViewProps> = ({
  result,
  onBack,
  onSave,
  isSaved,
  lang
}) => {
  const [isCopied, setIsCopied] = useState<boolean>(false);

  const handlePrint = () => {
    window.print();
  };

  const handleCopy = () => {
    const summary = `Land Record [${result.metadata.searchType}: ${result.metadata.searchNumber}]\n` +
      `District: ${result.metadata.districtName}\n` +
      `Block: ${result.metadata.blockName}\n` +
      `Mouza: ${result.metadata.mouzaName}\n` +
      `Date: ${new Date(result.metadata.timestamp).toLocaleString()}`;

    navigator.clipboard.writeText(summary);
    setIsCopied(true);
    setTimeout(() => setIsCopied(false), 2000);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-4 rounded-xl border border-slate-200 shadow-sm print:hidden">
        <button
          onClick={onBack}
          className="inline-flex items-center gap-2 text-sm font-semibold text-slate-600 hover:text-slate-900 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>{lang === 'bn' ? 'নতুন অনুসন্ধান করুন' : 'Back to Search'}</span>
        </button>

        <div className="flex items-center gap-2">
          <button
            onClick={() => onSave(result)}
            className={`inline-flex items-center gap-1.5 px-3.5 py-2 rounded-lg text-xs font-bold transition-all border ${
              isSaved
                ? 'bg-emerald-50 text-emerald-700 border-emerald-300'
                : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-50'
            }`}
          >
            {isSaved ? <BookmarkCheck className="w-4 h-4 text-emerald-600" /> : <Bookmark className="w-4 h-4" />}
            <span>{isSaved ? (lang === 'bn' ? 'সংরক্ষিত করা হয়েছে' : 'Saved in App') : (lang === 'bn' ? 'সংরক্ষণ করুন' : 'Save Record')}</span>
          </button>

          <button
            onClick={handleCopy}
            className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-lg text-xs font-bold bg-white text-slate-700 border border-slate-200 hover:bg-slate-50 transition-all"
          >
            {isCopied ? <Check className="w-4 h-4 text-emerald-600" /> : <Share2 className="w-4 h-4" />}
            <span>{isCopied ? (lang === 'bn' ? 'কপি হয়েছে' : 'Copied') : (lang === 'bn' ? 'শেয়ার' : 'Share')}</span>
          </button>

          <button
            onClick={handlePrint}
            className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg text-xs font-bold bg-slate-900 hover:bg-slate-800 text-white shadow-sm transition-all"
          >
            <Printer className="w-4 h-4" />
            <span>{lang === 'bn' ? 'প্রিন্ট / PDF ডাউনলোড' : 'Print / Export PDF'}</span>
          </button>
        </div>
      </div>

      <div id="printable-record" className="bg-white rounded-2xl shadow-xl border border-slate-200 overflow-hidden">
        <div className="bg-gradient-to-r from-emerald-800 via-emerald-700 to-emerald-900 text-white p-6 sm:p-8">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <span className="bg-emerald-500/30 text-emerald-100 text-xs px-2.5 py-0.5 rounded-full font-bold uppercase tracking-wider border border-emerald-400/30">
                  Government of West Bengal
                </span>
                <span className="flex items-center gap-1 text-xs text-emerald-200 font-medium">
                  <ShieldCheck className="w-3.5 h-3.5 text-emerald-300" />
                  Official RoR Verification
                </span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
                {lang === 'bn' ? 'ভূমি ও সম্পত্তি রেকর্ড ফলাফল' : 'Certified Land Record Result'}
              </h1>
              <p className="text-emerald-100 text-xs sm:text-sm mt-1">
                Banglarbhumi Khatian & Plot Information System
              </p>
            </div>

            <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 border border-white/20 text-right">
              <span className="text-[11px] text-emerald-200 uppercase tracking-wider block font-semibold">
                {result.metadata.searchType}
              </span>
              <span className="text-2xl font-black text-white font-mono tracking-tight">
                {result.metadata.searchNumber}
              </span>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-emerald-600/50 grid grid-cols-2 sm:grid-cols-4 gap-4 text-xs">
            <div>
              <span className="text-emerald-300 block">District (জেলা):</span>
              <span className="font-bold text-white text-sm">{result.metadata.districtName}</span>
            </div>
            <div>
              <span className="text-emerald-300 block">Block (ব্লক):</span>
              <span className="font-bold text-white text-sm">{result.metadata.blockName}</span>
            </div>
            <div>
              <span className="text-emerald-300 block">Mouza (মৌজা):</span>
              <span className="font-bold text-white text-sm">{result.metadata.mouzaName}</span>
            </div>
            <div>
              <span className="text-emerald-300 block">Record Query Date:</span>
              <span className="font-mono text-white text-sm">
                {new Date(result.metadata.timestamp).toLocaleDateString('en-IN', {
                  day: '2-digit',
                  month: 'short',
                  year: 'numeric'
                })}
              </span>
            </div>
          </div>
        </div>

        <div className="p-6 sm:p-8">
          <div
            className="prose max-w-none text-slate-800"
            dangerouslySetInnerHTML={{ __html: result.html }}
          />
        </div>

        <div className="bg-slate-50 border-t border-slate-200 p-4 text-center text-xs text-slate-500 flex flex-col sm:flex-row items-center justify-between gap-2 px-6">
          <div className="flex items-center gap-1">
            <MapPin className="w-3.5 h-3.5 text-emerald-600" />
            <span>Digital Land Governance Infrastructure, BanglarBhumi-Pro</span>
          </div>
          <div>
            <span>Verified Source: {result.metadata.source}</span>
          </div>
        </div>
      </div>
    </div>
  );
};
